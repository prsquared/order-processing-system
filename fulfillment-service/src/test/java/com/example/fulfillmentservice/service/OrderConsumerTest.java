package com.example.fulfillmentservice.service;

import com.example.fulfillmentservice.event.OrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

class OrderConsumerTest {

    @Mock
    private FulfillmentService fulfillmentService;

    @InjectMocks
    private OrderConsumer orderConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsume_Success() {
        // Arrange
        OrderEvent event = new OrderEvent(1L, "CUST-1", List.of("item1"), new BigDecimal("10.0"), "PENDING");

        // Act
        orderConsumer.consume(event);

        // Assert
        verify(fulfillmentService, times(1)).processOrder(event);
    }

    @Test
    void testConsume_Exception() {
        // Arrange
        OrderEvent event = new OrderEvent(1L, "CUST-1", List.of("item1"), new BigDecimal("10.0"), "PENDING");
        doThrow(new RuntimeException("Test Exception")).when(fulfillmentService).processOrder(event);

        // Act
        orderConsumer.consume(event);

        // Assert
        verify(fulfillmentService, times(1)).processOrder(event);
        // Exception should be caught and logged
    }
}
