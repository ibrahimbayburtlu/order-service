package com.getirCase.order_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getirCase.order_service.enums.KafkaEventType;
import com.getirCase.order_service.enums.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendEvent(KafkaEventType event, String topic) {
        try {
            String eventJson = objectMapper.writeValueAsString(Map.of("eventType", event.name()));
            kafkaTemplate.send(topic, eventJson);
            log.info("Sent Kafka event to topic {}: {}", topic, eventJson);
        } catch (Exception e) {
            log.error("Failed to send Kafka event to topic {}", topic, e);
        }
    }

}
