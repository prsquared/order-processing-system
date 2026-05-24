package com.example.orderservice.controller;

import com.example.orderservice.entity.Order;
import com.example.orderservice.event.OrderEvent;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProducer orderProducer;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        // Arrange
        Order inputOrder = new Order();
        inputOrder.setCustomerId("CUST-123");
        inputOrder.setItems(List.of("ITEM1", "ITEM2"));
        inputOrder.setTotalAmount(new BigDecimal("100.00"));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setCustomerId("CUST-123");
        savedOrder.setItems(List.of("ITEM1", "ITEM2"));
        savedOrder.setTotalAmount(new BigDecimal("100.00"));
        savedOrder.setStatus("PENDING");

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        ResponseEntity<Order> response = orderController.createOrder(inputOrder);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1L, response.getBody().getId());
        assertEquals("PENDING", response.getBody().getStatus());

        verify(orderRepository).save(inputOrder);
        verify(orderProducer).sendOrderEvent(any(OrderEvent.class));
    }

    @Test
    void testGetDynamicMessage() {
        // Arrange
        ReflectionTestUtils.setField(orderController, "dynamicMessage", "Test Message");

        // Act
        String message = orderController.getDynamicMessage();

        // Assert
        assertEquals("Test Message", message);
    }
}
