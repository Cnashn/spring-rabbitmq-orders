package com.example.orders;

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.entity.OrderStatus;
import com.example.orders.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
    }

    @LocalServerPort int port;
    @Autowired TestRestTemplate restTemplate;
    @Autowired OrderRepository orderRepository;

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
