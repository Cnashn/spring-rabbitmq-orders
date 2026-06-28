package com.example.orders.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrderEvent {

    private UUID eventId;
    private UUID orderId;
    private String product;
    private Integer quantity;
    private BigDecimal price;
    private Instant occurredAt;

    public OrderEvent() {}

    public OrderEvent(UUID orderId, String product, Integer quantity, BigDecimal price) {
        this.eventId = UUID.randomUUID();
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.occurredAt = Instant.now();
    }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
}
