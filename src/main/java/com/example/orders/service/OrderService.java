package com.example.orders.service;

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.entity.Order;
import com.example.orders.messaging.OrderEvent;
import com.example.orders.messaging.OrderEventPublisher;
import com.example.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher publisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher publisher) {
        this.orderRepository = orderRepository;
        this.publisher = publisher;
    }

    public OrderResponse create(CreateOrderRequest request) {
        Order order = new Order();
        order.setProduct(request.getProduct());
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());
        Order saved = orderRepository.save(order);
        publisher.publish(new OrderEvent(saved.getId(), saved.getProduct(), saved.getQuantity(), saved.getPrice()));
        return OrderResponse.from(saved);
    }

    public OrderResponse findById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return OrderResponse.from(order);
    }
}
