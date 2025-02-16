package com.getirCase.order_service.model.event;

import com.getirCase.order_service.enums.KafkaEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class KafkaEvent {
    private KafkaEventType eventType;
}
