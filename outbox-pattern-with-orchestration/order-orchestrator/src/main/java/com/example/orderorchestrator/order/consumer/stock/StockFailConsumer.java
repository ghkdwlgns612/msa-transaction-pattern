package com.example.orderorchestrator.order.consumer.stock;

import com.example.kafka.dto.orchestratortoorder.OrderResultResponse;
import com.example.kafka.dto.orchestratortopayment.PaymentCompensationRequest;
import com.example.kafka.dto.orchestratortostock.OrchestratorToStockRequest;
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
import static com.example.kafka.KafkaConstants.ORCHESTRATOR_STOCK_FAIL_CONSUMER_CONTAINER_NAME;
import static com.example.kafka.KafkaConstants.PAYMENT_COMPENSATION_TOPIC_NAME;
import static com.example.kafka.KafkaConstants.STOCK_DLQ_TOPIC_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockFailConsumer {

    private final OrderForResponseRepository orderForResponseRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @KafkaListener(
            topics = STOCK_DLQ_TOPIC_NAME,
            containerFactory = ORCHESTRATOR_STOCK_FAIL_CONSUMER_CONTAINER_NAME)
    void listen(OrchestratorToStockRequest request) {
        log.error("Stock Failed: {}", request);
        OrderForResponse order = orderForResponseRepository.findById(request.getOrderId())
                .orElseThrow(EntityNotFoundException::new);
        order.failStock();
        if (order.isSucceedPayment()) {
            kafkaTemplate.send(
                    PAYMENT_COMPENSATION_TOPIC_NAME,
                    new PaymentCompensationRequest(order.getUserName(), order.getPrice()));
        }

        kafkaTemplate.send(
                ORCHESTRATOR_ORDER_RESPONSE_TOPIC_NAME,
                new OrderResultResponse(order.getOrderId(), false));
        orderForResponseRepository.save(order);
    }
}
