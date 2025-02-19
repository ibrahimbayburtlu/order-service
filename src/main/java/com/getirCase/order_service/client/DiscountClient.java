package com.getirCase.order_service.client;

import com.getirCase.order_service.model.request.DiscountRequest;
import com.getirCase.order_service.model.response.DiscountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static com.getirCase.order_service.constants.ApiEndpoints.*;

@FeignClient(name = "discount-service", url = DISCOUNT_URL)
public interface DiscountClient {

    @GetMapping()
    DiscountResponse getDiscount(DiscountRequest request);

}
