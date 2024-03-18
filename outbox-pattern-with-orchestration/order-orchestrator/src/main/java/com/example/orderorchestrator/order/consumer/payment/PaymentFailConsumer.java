package com.example.orderorchestrator.order.consumer.payment;

import com.example.kafka.dto.orchestratortoorder.OrderResultResponse;
import com.example.kafka.dto.orchestratortopayment.OrchestratorToPaymentRequest;
import com.example.kafka.dto.orchestratortostock.StockCompensationRequest;
import com.example.orderorchestrator.order.orchestrator.OrderForResponse;
import com.example.orderorchestrator.order.orchestrator.OrderForResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.kafka.KafkaConstants.ORCHESTRATOR_ORDER_RESPONSE_TOPIC_NAME;
import static com.example.kafka.KafkaConstants.ORCHESTRATOR_PAYMENT_FAIL_CONSUMER_CONTAINER_NAME;
import static com.example.kafka.KafkaConstants.PAYMENT_DLQ_TOPIC_NAME;
import static com.example.kafka.KafkaConstants.STOCK_COMPENSATION_TOPIC_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentFailConsumer {

    private final OrderForResponseRepository orderForResponseRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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
            kafkaTemplate.send(
                    STOCK_COMPENSATION_TOPIC_NAME,
                    new StockCompensationRequest(order.getItemName(), order.getQuantity()));
        }

        kafkaTemplate.send(
                ORCHESTRATOR_ORDER_RESPONSE_TOPIC_NAME,
                new OrderResultResponse(order.getOrderId(), false));
        orderForResponseRepository.save(order);
    }
}
