package com.example.fulfillmentservice.service;

import com.example.fulfillmentservice.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderConsumer {

    private final FulfillmentService fulfillmentService;

    public OrderConsumer(FulfillmentService fulfillmentService) {
        this.fulfillmentService = fulfillmentService;
    }

    @KafkaListener(topics = "orders-topic", groupId = "fulfillment-group")
    public void consume(OrderEvent orderEvent) {
        log.info("Consumed message: {}", orderEvent);
        try {
            fulfillmentService.processOrder(orderEvent);
        } catch (Exception e) {
            log.error("Error processing order: {}", orderEvent.getOrderId(), e);
        }
    }
}
