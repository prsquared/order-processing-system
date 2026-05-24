package com.example.orderservice.service;

import com.example.orderservice.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderProducer {

    private static final String TOPIC = "orders-topic";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderEvent orderEvent) {
        log.info("Producing order event: {}", orderEvent);
        kafkaTemplate.send(TOPIC, String.valueOf(orderEvent.getOrderId()), orderEvent);
    }
}
