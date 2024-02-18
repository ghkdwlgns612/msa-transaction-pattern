package com.example.paymentservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderConsumer {

    @KafkaListener(topics = "payment")
    public void listener(Object data) {
        log.info("Data consuming: {}", data.toString());
    }
}
