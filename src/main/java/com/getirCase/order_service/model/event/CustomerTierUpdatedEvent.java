package com.getirCase.order_service.model.event;

import com.getirCase.order_service.enums.KafkaEventType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CustomerTierUpdatedEvent extends KafkaEvent {
    private Long customerId;
    private int orderCount;

    public CustomerTierUpdatedEvent(Long customerId, int orderCount) {
        this.setEventType(KafkaEventType.CUSTOMER_TIER_UPDATED_EVENT);
        this.customerId = customerId;
        this.orderCount = orderCount;
    }
}
