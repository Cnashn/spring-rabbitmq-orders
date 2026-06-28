package com.example.orders.dto;

import com.example.orders.entity.Order;
import com.example.orders.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrderResponse {

    private UUID id;
    private String product;
    private Integer quantity;
    private BigDecimal price;
    private OrderStatus status;
    private Instant createdAt;

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.id = order.getId();
        r.product = order.getProduct();
        r.quantity = order.getQuantity();
        r.price = order.getPrice();
        r.status = order.getStatus();
        r.createdAt = order.getCreatedAt();
        return r;
    }

    public UUID getId() { return id; }
    public String getProduct() { return product; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
