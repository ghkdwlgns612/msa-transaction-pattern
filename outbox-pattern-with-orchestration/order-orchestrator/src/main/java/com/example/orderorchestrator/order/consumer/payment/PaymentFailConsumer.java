package com.example.orderorchestrator.order.consumer.payment;

import com.example.kafka.dto.orchestratortopayment.OrchestratorToPaymentRequest;
import com.example.orderorchestrator.order.orchestrator.OrderForResponse;
import com.example.orderorchestrator.order.orchestrator.OrderForResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.kafka.KafkaConstants.ORCHESTRATOR_PAYMENT_FAIL_CONSUMER_CONTAINER_NAME;
import static com.example.kafka.KafkaConstants.PAYMENT_DLQ_TOPIC_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentFailConsumer {

    private final OrderForResponseRepository orderForResponseRepository;

    @Transactional
    @KafkaListener(
            topics = PAYMENT_DLQ_TOPIC_NAME,
            containerFactory = ORCHESTRATOR_PAYMENT_FAIL_CONSUMER_CONTAINER_NAME)
    void listen(OrchestratorToPaymentRequest request) {
        log.error("Payment Failed: {}", request);
        OrderForResponse order = orderForResponseRepository.findById(request.getOrderId())
                .orElseThrow(EntityNotFoundException::new);
        order.failPayment();
        if (order.isSucceedStock()) {
            // stock compensation
        }

        orderForResponseRepository.save(order);
    }
}
