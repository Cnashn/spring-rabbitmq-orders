package com.example.orders.controller;

import com.example.orders.dto.OrderResponse;
import com.example.orders.entity.OrderStatus;
import com.example.orders.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean OrderService orderService;

    @Test
    void createOrder_validRequest_returns201() throws Exception {
        OrderResponse response = buildResponse("Laptop", OrderStatus.PENDING);
        when(orderService.create(any())).thenReturn(response);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"product":"Laptop","quantity":2,"price":"999.99"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.product").value("Laptop"));
    }

    @Test
    void createOrder_blankProduct_returns400() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"product":"","quantity":2,"price":"999.99"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_zeroQuantity_returns400() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"product":"Laptop","quantity":0,"price":"999.99"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrder_existingId_returns200() throws Exception {
        OrderResponse response = buildResponse("Laptop", OrderStatus.PROCESSED);
        when(orderService.findById(any())).thenReturn(response);

        mockMvc.perform(get("/orders/{id}", response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSED"));
    }

    private OrderResponse buildResponse(String product, OrderStatus status) {
        com.example.orders.entity.Order order = new com.example.orders.entity.Order();
        order.setProduct(product);
        order.setQuantity(1);
        order.setPrice(BigDecimal.valueOf(99.99));
        order.setStatus(status);
        try {
            var idField = order.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(order, UUID.randomUUID());
            var createdAtField = order.getClass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(order, Instant.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return OrderResponse.from(order);
    }
}
