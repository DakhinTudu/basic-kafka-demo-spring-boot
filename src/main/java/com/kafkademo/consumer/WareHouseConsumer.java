package com.kafkademo.consumer;

import com.kafkademo.dto.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class WareHouseConsumer {
    @KafkaListener(topics = "orders",groupId = "warehouse-group")
    public void handleOrder(OrderPlacedEvent event){
        System.out.println("[WAREHOUSE] " + event.orderId());
    }
}
