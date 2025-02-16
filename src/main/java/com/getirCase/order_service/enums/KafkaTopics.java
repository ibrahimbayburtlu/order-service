package com.getirCase.order_service.enums;

import lombok.Getter;

@Getter
public enum KafkaTopics {
    CUSTOMER_EVENTS("customer.events");

    private final String topicName;

    KafkaTopics(String topicName) {
        this.topicName = topicName;
    }
}
