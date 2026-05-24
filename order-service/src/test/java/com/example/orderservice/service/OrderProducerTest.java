package com.example.orderservice.service;

import com.example.orderservice.event.OrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class OrderProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OrderProducer orderProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendOrderEvent() {
        // Arrange
        OrderEvent event = new OrderEvent(1L, "CUST-1", List.of("ITEM"), new BigDecimal("10.0"), "PENDING");

        // Act
        orderProducer.sendOrderEvent(event);

        // Assert
        verify(kafkaTemplate).send(eq("orders-topic"), eq("1"), eq(event));
    }
}
