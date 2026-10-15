package com.kafkademo.controller;

import com.kafkademo.dto.OrderPlacedEvent;
import com.kafkademo.dto.OrderRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    public OrderController(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public String placeOrder(@RequestBody OrderRequest request){
       OrderPlacedEvent event = OrderPlacedEvent.from(request);
       kafkaTemplate.send("orders",request.orderId(),event).whenComplete((result,ex)->{
           if (ex != null) {
               System.out.println(
                       "Failed to publish eventId=" + event.eventId()
                               + ", error=" + ex.getMessage()
               );
           } else {
               System.out.println(
                       "Published eventId=" + event.eventId()
                               + ", orderId=" + event.orderId()
                               + ", partition=" + result.getRecordMetadata().partition()
                               + ", offset=" + result.getRecordMetadata().offset()
               );
           }

       });
       return "Published event "+ event.eventId();
    }
}
