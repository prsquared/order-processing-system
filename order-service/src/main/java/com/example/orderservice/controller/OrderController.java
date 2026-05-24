package com.example.orderservice.controller;

import com.example.orderservice.entity.Order;
import com.example.orderservice.event.OrderEvent;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RefreshScope
@Slf4j
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    @Value("${app.dynamic.message:Default Message}")
    private String dynamicMessage;

    public OrderController(OrderRepository orderRepository, OrderProducer orderProducer) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        log.info("Received request to create order on Virtual Thread: {}", Thread.currentThread().isVirtual());
        
        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent(
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getItems(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus()
        );
        
        orderProducer.sendOrderEvent(event);

        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/config-message")
    public String getDynamicMessage() {
        return dynamicMessage;
    }
}
