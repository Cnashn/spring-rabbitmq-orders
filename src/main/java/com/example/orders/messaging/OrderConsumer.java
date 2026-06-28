package com.example.orders.messaging;

import com.example.orders.config.RabbitConfig;
import com.example.orders.service.OrderProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    private final OrderProcessingService processingService;

    public OrderConsumer(OrderProcessingService processingService) {
        this.processingService = processingService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void consume(OrderEvent event) {
        log.info("Received event for order {}", event.getOrderId());
        processingService.process(event);
        log.info("Order {} marked PROCESSED", event.getOrderId());
    }
}
