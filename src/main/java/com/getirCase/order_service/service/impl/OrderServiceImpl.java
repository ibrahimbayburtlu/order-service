package com.getirCase.order_service.service.impl;

import com.getirCase.order_service.client.CustomerClient;
import com.getirCase.order_service.client.DiscountClient;
import com.getirCase.order_service.entity.Order;
import com.getirCase.order_service.enums.CustomerTier;
import com.getirCase.order_service.enums.KafkaTopics;
import com.getirCase.order_service.enums.OrderStatus;
import com.getirCase.order_service.exception.CustomerNotFoundException;
import com.getirCase.order_service.exception.DiscountServiceException;
import com.getirCase.order_service.exception.OrderNotFoundException;
import com.getirCase.order_service.model.event.CustomerOrderCreatedEvent;
import com.getirCase.order_service.model.request.DiscountRequest;
import com.getirCase.order_service.model.request.OrderRequest;
import com.getirCase.order_service.model.response.CustomerResponse;
import com.getirCase.order_service.model.response.DiscountResponse;
import com.getirCase.order_service.model.response.OrderResponse;
import com.getirCase.order_service.repository.OrderRepository;
import com.getirCase.order_service.service.KafkaProducerService;
import com.getirCase.order_service.service.OrderService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final KafkaProducerService kafkaProducerService;
    private final DiscountClient discountClient;

    /**
     * Retrieve an order by ID.
     */
    @Override
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getOrderDate(),
                order.getStatus()
        );
    }


    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        logger.info("Fetching customer info for ID: {}", orderRequest.getCustomerId());

        CustomerResponse customer;
        try {
            customer = customerClient.getCustomerById(orderRequest.getCustomerId());
        } catch (FeignException.NotFound e) {
            throw new CustomerNotFoundException("Customer with ID " + orderRequest.getCustomerId() + " not found.");
        }


        Order newOrder = new Order();
        newOrder.setCustomerId(orderRequest.getCustomerId());
        newOrder.setTotalAmount(orderRequest.getTotalAmount());
        newOrder.setDiscountAmount(BigDecimal.ZERO); // Default discount
        newOrder.setStatus(OrderStatus.PENDING);


        Order savedOrder = orderRepository.save(newOrder);
        logger.info("Order created successfully with ID: {}", savedOrder.getOrderId());


        DiscountRequest discountRequest = DiscountRequest.builder()
                .customerId(orderRequest.getCustomerId())
                .orderId(savedOrder.getOrderId()) // Use the real orderId
                .customerTier(customer.getTier()) // Fetch customer tier
                .amountBeforeDiscount(orderRequest.getTotalAmount())
                .build();


        DiscountResponse discountResponse;
        try {
            discountResponse = discountClient.getDiscount(discountRequest);
        } catch (FeignException e) {
            logger.error("Failed to fetch discount for order ID: {}", savedOrder.getOrderId(), e);
            throw new DiscountServiceException("Failed to fetch discount information");
        }


        BigDecimal discountPercent = discountResponse.getDiscountPercent();
        if (discountPercent == null) {
            logger.warn("Discount percent is null. Defaulting to 0%");
            discountPercent = BigDecimal.ZERO;
        }


        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal amountAfterDiscount = savedOrder.getTotalAmount();

        if (discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = savedOrder.getTotalAmount()
                    .multiply(discountPercent)
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            amountAfterDiscount = savedOrder.getTotalAmount().subtract(discountAmount);
        }


        savedOrder.setDiscountAmount(discountAmount);
        savedOrder.setTotalAmount(amountAfterDiscount);
        savedOrder.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(savedOrder);

        CustomerOrderCreatedEvent orderCreatedEvent = CustomerOrderCreatedEvent.builder()
                .orderId(savedOrder.getOrderId())
                .customerId(savedOrder.getCustomerId())
                .totalAmount(savedOrder.getTotalAmount())
                .discountAmount(savedOrder.getDiscountAmount())
                .build();

        kafkaProducerService.sendEvent(orderCreatedEvent, KafkaTopics.CUSTOMER_EVENTS.getTopicName());

        return OrderResponse.builder()
                .orderId(savedOrder.getOrderId())
                .customerId(savedOrder.getCustomerId())
                .totalAmount(amountAfterDiscount)
                .discountAmount(discountAmount)
                .orderDate(savedOrder.getOrderDate())
                .status(savedOrder.getStatus())
                .build();
    }




    /**
     * Cancel an order.
     */
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        logger.info("Cancelling order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Completed orders cannot be cancelled.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        logger.info("Order with ID {} has been cancelled.", orderId);

        new OrderResponse(
                order.getOrderId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getOrderDate(),
                order.getStatus()
        );
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(Long orderId, OrderRequest orderRequest) {
        logger.info("Updating order with ID: {}", orderId);


        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));


        if (existingOrder.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Completed orders cannot be updated.");
        }

        existingOrder.setTotalAmount(orderRequest.getTotalAmount());

        if (!existingOrder.getCustomerId().equals(orderRequest.getCustomerId())) {
            CustomerResponse customer = customerClient.getCustomerById(orderRequest.getCustomerId());
            BigDecimal discount = calculateDiscount(customer.getTier(), orderRequest.getTotalAmount());
            BigDecimal finalAmount = orderRequest.getTotalAmount().subtract(discount);

            existingOrder.setCustomerId(orderRequest.getCustomerId());
            existingOrder.setTotalAmount(finalAmount);
            existingOrder.setDiscountAmount(discount);
        }


        Order updatedOrder = orderRepository.save(existingOrder);

        logger.info("Order with ID {} has been updated successfully.", updatedOrder.getOrderId());

        return new OrderResponse(
                updatedOrder.getOrderId(),
                updatedOrder.getCustomerId(),
                updatedOrder.getTotalAmount(),
                updatedOrder.getDiscountAmount(),
                updatedOrder.getOrderDate(),
                updatedOrder.getStatus()
        );
    }


    /**
     * Delete an order.
     */
    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        logger.info("Deleting order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        orderRepository.delete(order);
        logger.info("Order with ID {} has been deleted.", orderId);
    }

    /**
     * Calculate discount based on customer tier.
     */
    private BigDecimal calculateDiscount(CustomerTier tier, BigDecimal amount) {
        if (tier == CustomerTier.GOLD) {
            return amount.multiply(BigDecimal.valueOf(0.10));
        } else if (tier == CustomerTier.PLATINUM) {
            return amount.multiply(BigDecimal.valueOf(0.20));
        }
        return BigDecimal.ZERO;
    }
}
