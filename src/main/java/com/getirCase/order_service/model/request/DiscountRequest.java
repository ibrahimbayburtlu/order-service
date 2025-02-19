package com.getirCase.order_service.model.request;

import com.getirCase.order_service.enums.CustomerTier;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest {

    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;

    @NotNull(message = "Order ID cannot be null")
    private Long orderId;

    @NotBlank(message = "Customer tier must be specified")
    private CustomerTier customerTier;

    @NotNull(message = "Amount before discount cannot be null")
    @Min(value = 1, message = "Amount before discount must be at least 1")
    private BigDecimal amountBeforeDiscount;

}
