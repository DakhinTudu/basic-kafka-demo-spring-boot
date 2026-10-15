package com.kafkademo.dto;

import java.util.UUID;

public record OrderPlacedEvent(
        String eventId,
        String orderId,
        String item,
        int quantity,
        long timestamp
) {
    public static OrderPlacedEvent from(OrderRequest request){
        return new OrderPlacedEvent(
                UUID.randomUUID().toString(),
                request.orderId(),
                request.item(),
                request.quantity(),
                System.currentTimeMillis()
        );
    }
}
