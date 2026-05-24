package com.example.fulfillmentservice.service;

import com.example.fulfillmentservice.event.OrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class FulfillmentServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private FulfillmentService fulfillmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessOrder() {
        // Arrange
        OrderEvent event = new OrderEvent(1L, "CUST-1", List.of("item1", "longitem2"), new BigDecimal("10.0"), "PENDING");

        // Act
        fulfillmentService.processOrder(event);

        // Assert
        verify(jdbcTemplate).update(anyString(), eq(1L), eq("PROCESSED"), eq("{5=[ITEM1], 9=[LONGITEM2]}"));
    }
}
