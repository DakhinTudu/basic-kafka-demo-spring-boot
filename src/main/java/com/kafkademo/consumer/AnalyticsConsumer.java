package com.kafkademo.consumer;

import com.kafkademo.dto.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsConsumer {

    @KafkaListener(topics = "orders",groupId = "analytics-group")
    public void handleOrder(OrderPlacedEvent event){
        System.out.println("[ANALYTICS] " + event.orderId());
    }
}
