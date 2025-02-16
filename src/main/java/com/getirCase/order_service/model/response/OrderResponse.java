package com.getirCase.order_service.model.response;

import com.getirCase.order_service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private Long customerId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private LocalDateTime orderDate;
    private OrderStatus status;

}