package com.example.orderorchestrator.order.consumer.payment;

import com.example.kafka.KafkaConstants;
import com.example.kafka.dto.common.OrderSuccessResponse;
import com.example.kafka.dto.orchestratortoorder.OrderResultResponse;
import com.example.orderorchestrator.order.orchestrator.OrderForResponse;
import com.example.orderorchestrator.order.orchestrator.OrderForResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.kafka.KafkaConstants.ORCHESTRATOR_PAYMENT_SUCCESS_CONSUMER_CONTAINER_NAME;
import static com.example.kafka.KafkaConstants.PAYMENT_SUCCESS_TOPIC_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final OrderForResponseRepository orderForResponseRepository;
    private final KafkaTemplate<String, OrderResultResponse> kafkaTemplate;

    @Transactional
    @KafkaListener(
            topics = PAYMENT_SUCCESS_TOPIC_NAME,
            containerFactory = ORCHESTRATOR_PAYMENT_SUCCESS_CONSUMER_CONTAINER_NAME)
    void listen(OrderSuccessResponse response) {
        log.info("Success payment: {}", response);
        OrderForResponse order = orderForResponseRepository.findById(response.getOrderId())
                .orElseThrow(EntityNotFoundException::new);

        order.successPayment();
        if (order.isSucceedStock()) {
            kafkaTemplate.send(
                    KafkaConstants.ORCHESTRATOR_ORDER_RESPONSE_TOPIC_NAME,
                    new OrderResultResponse(order.getOrderId(), true));
        }

        orderForResponseRepository.save(order);
    }
}
