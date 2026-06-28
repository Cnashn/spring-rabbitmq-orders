package com.example.orders.service;

import com.example.orders.entity.Order;
import com.example.orders.entity.OrderStatus;
import com.example.orders.messaging.OrderEvent;
import com.example.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderProcessingService {

    private final OrderRepository orderRepository;

    public OrderProcessingService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void process(OrderEvent event) {
        if ("FAIL".equals(event.getProduct())) {
            throw new RuntimeException("Forced failure for product: FAIL");
        }

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + event.getOrderId()));

        order.setStatus(OrderStatus.PROCESSED);
        orderRepository.save(order);
    }
}
