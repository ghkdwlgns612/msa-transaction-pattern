package com.example.orderservice.consumer;

import com.example.kafka.dto.orchestratortoorder.OrderResultResponse;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.kafka.KafkaConstants.ORCHESTRATOR_ORDER_RESPONSE_TOPIC_NAME;
import static com.example.kafka.KafkaConstants.ORDER_ORCHESTRATOR_CONSUMER_CONTAINER_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderResultConsumer {

    private final OrderRepository orderRepository;

    @Transactional
    @KafkaListener(topics = ORCHESTRATOR_ORDER_RESPONSE_TOPIC_NAME, containerFactory = ORDER_ORCHESTRATOR_CONSUMER_CONTAINER_NAME)
    public void listenResponse(OrderResultResponse response) {
        log.info("Response Data consuming: {}", response);
        if (response == null) {
            throw new IllegalArgumentException("Response is null");
        }

        Order order = orderRepository.findById(response.getOrderId())
                .orElseThrow(EntityNotFoundException::new);

        order.updateStatus(response.isSucceed());
        orderRepository.save(order);
    }
}
