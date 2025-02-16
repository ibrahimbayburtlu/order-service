package com.getirCase.order_service.service;

import com.getirCase.order_service.model.response.OrderResponse;
import com.getirCase.order_service.model.request.OrderRequest;

public interface OrderService {

    OrderResponse getOrder(Long orderId);
    OrderResponse createOrder(OrderRequest orderRequest);
    void cancelOrder(Long orderId);
    OrderResponse updateOrder(Long orderId, OrderRequest orderRequest);
    void deleteOrder(Long orderId);
}
