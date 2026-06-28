package com.example.orders.service;

import com.example.orders.entity.Order;
import com.example.orders.entity.OrderStatus;
import com.example.orders.entity.ProcessedEvent;
import com.example.orders.messaging.OrderEvent;
import com.example.orders.repository.OrderRepository;
import com.example.orders.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;

    public OrderProcessingService(OrderRepository orderRepository,
                                  ProcessedEventRepository processedEventRepository) {
        this.orderRepository = orderRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void process(OrderEvent event) {
        if (processedEventRepository.existsById(event.getEventId())) {
            log.info("Duplicate event {}, skipping", event.getEventId());
            return;
        }

        if ("FAIL".equals(event.getProduct())) {
            throw new RuntimeException("Forced failure for product: FAIL");
        }

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + event.getOrderId()));

        order.setStatus(OrderStatus.PROCESSED);
        orderRepository.save(order);
        processedEventRepository.save(new ProcessedEvent(event.getEventId()));
    }
}
