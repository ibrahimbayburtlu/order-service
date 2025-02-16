package com.getirCase.order_service.model.event;

import com.getirCase.order_service.enums.KafkaEventType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CustomerOrderCreatedEvent extends KafkaEvent {
    private Long orderId;
    private Long customerId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;

    public CustomerOrderCreatedEvent(Long orderId, Long customerId, BigDecimal totalAmount, BigDecimal discountAmount) {
        this.setEventType(KafkaEventType.CUSTOMER_ORDER_CREATED_EVENT);
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
    }
}
