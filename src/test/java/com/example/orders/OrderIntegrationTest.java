package com.example.orders;

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5433/orders",
        "spring.datasource.username=orders",
        "spring.datasource.password=orders",
        "spring.rabbitmq.host=localhost",
        "spring.rabbitmq.port=5672"
})
class OrderIntegrationTest {

    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;

    @Test
    void createOrder_fullRoundTrip_orderIsProcessed() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setProduct("Monitor");
        request.setQuantity(1);
        request.setPrice(new BigDecimal("299.99"));

        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/orders",
                request,
                OrderResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UUID orderId = response.getBody().getId();
        assertThat(orderId).isNotNull();

        await().atMost(10, SECONDS).untilAsserted(() -> {
            OrderResponse fetched = restTemplate
                    .getForObject("http://localhost:" + port + "/orders/" + orderId, OrderResponse.class);
            assertThat(fetched.getStatus()).isEqualTo(OrderStatus.PROCESSED);
        });
    }
}
