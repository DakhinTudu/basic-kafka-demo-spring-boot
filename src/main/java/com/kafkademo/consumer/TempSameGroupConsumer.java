package com.kafkademo.consumer;

import com.kafkademo.dto.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TempSameGroupConsumer {

    @KafkaListener(topics = "orders",groupId = "email-group")
    public void handleOrder(OrderPlacedEvent event){
        System.out.println("[TEMP_SAME_GRP_CONSUMER]"+"eventId : "+event.eventId()+
                " orderId : "+event.orderId()+
                " item : "+event.item()+
                " quantity : "+event.quantity());
    }
}
