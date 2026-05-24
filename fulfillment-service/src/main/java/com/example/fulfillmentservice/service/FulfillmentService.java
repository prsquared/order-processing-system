package com.example.fulfillmentservice.service;

import com.example.fulfillmentservice.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FulfillmentService {

    private final JdbcTemplate jdbcTemplate;

    public FulfillmentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS fulfillment_status (order_id BIGINT PRIMARY KEY, status VARCHAR(255), processed_items TEXT)");
    }

    public void processOrder(OrderEvent orderEvent) {
        log.info("Processing order: {}", orderEvent.getOrderId());

        // Demonstrate Java 8+ Streams API
        // E.g., Filter items that start with a specific prefix, group them, or transform them
        List<String> items = orderEvent.getItems();
        
        String processedItemsString = "";
        if (items != null) {
            Map<Integer, List<String>> groupedByLength = items.stream()
                    .map(String::toUpperCase)
                    .filter(item -> !item.isBlank())
                    .collect(Collectors.groupingBy(String::length));
            
            processedItemsString = groupedByLength.toString();
            log.info("Processed items using Streams API: {}", processedItemsString);
        }

        // Demonstrate JdbcTemplate
        String sql = "INSERT INTO fulfillment_status (order_id, status, processed_items) VALUES (?, ?, ?) " +
                     "ON CONFLICT (order_id) DO UPDATE SET status = EXCLUDED.status, processed_items = EXCLUDED.processed_items";
        
        jdbcTemplate.update(sql, orderEvent.getOrderId(), "PROCESSED", processedItemsString);
        log.info("Order {} marked as PROCESSED in fulfillment database", orderEvent.getOrderId());
    }
}
