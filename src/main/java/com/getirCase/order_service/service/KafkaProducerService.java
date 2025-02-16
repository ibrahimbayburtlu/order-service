package com.getirCase.order_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getirCase.order_service.enums.KafkaEventType;
import com.getirCase.order_service.model.event.KafkaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public <T extends KafkaEvent> void sendEvent(T event, String topic) {
        try {
            String eventJson = objectMapper.writeValueAsString(event); // Artık tüm alanlar JSON'a çevrilecek
            kafkaTemplate.send(topic, eventJson);
            log.info("Sent Kafka event to topic {}: {}", topic, eventJson);
        } catch (Exception e) {
            log.error("Failed to send Kafka event to topic {}", topic, e);
        }
    }



}
