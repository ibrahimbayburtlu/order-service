package com.getirCase.order_service.client;

import com.getirCase.order_service.model.request.OrderCountRequest;
import com.getirCase.order_service.model.response.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static com.getirCase.order_service.constants.ApiEndpoints.*;

@FeignClient(name = "customer-management-service", url = CUSTOMER_URL)
public interface CustomerClient {

    @GetMapping(GET_CUSTOMER)
    CustomerResponse getCustomerById(@PathVariable("id") Long customerId);

    @PatchMapping(UPDATE_TIER)
    CustomerResponse updateCustomerTier(@PathVariable Long id, @RequestParam int orderCount);

    @PutMapping(UPDATE_ORDER_COUNT)
    CustomerResponse updateOrderCount(@PathVariable Long id,  @RequestBody OrderCountRequest request);

}
