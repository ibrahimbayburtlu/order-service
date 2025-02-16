package com.getirCase.order_service.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCountRequest {
    private int newOrderCount;
}
