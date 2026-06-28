package com.example.orders.messaging;

import com.example.orders.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterConsumer.class);

    @RabbitListener(queues = RabbitConfig.DLQ)
    public void consume(OrderEvent event) {
        log.error("Dead-lettered order {}: product='{}' could not be processed after retries",
                event.getOrderId(), event.getProduct());
    }
}
