package com.example.orderorchestrator.order.consumer.order;

import com.example.kafka.dto.ordertoorchestrator.OrderToOrchestratorRequest;
import com.example.orderorchestrator.order.Status;
import com.example.orderorchestrator.order.orchestrator.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.kafka.KafkaConstants.ORCHESTRATOR_ORDER_CONSUMER_CONTAINER_NAME;
import static com.example.kafka.KafkaConstants.ORCHESTRATOR_TOPIC_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final StockOutboxRepository stockOutboxRepository;
    private final OrderForResponseRepository orderForResponseRepository;

    @Transactional
    @KafkaListener(topics = ORCHESTRATOR_TOPIC_NAME, containerFactory = ORCHESTRATOR_ORDER_CONSUMER_CONTAINER_NAME)
    void listenOrder(OrderToOrchestratorRequest request) {
        log.info("Consume Data: {}", request);

        PaymentOutbox payment = new PaymentOutbox(
                request.getOrderId(),
                request.getUsername(),
                request.getPrice(),
                LocalDateTime.now());
        StockOutbox stock = new StockOutbox(
                request.getOrderId(),
                request.getItemName(),
                request.getQuantity(),
                LocalDateTime.now());
        paymentOutboxRepository.save(payment);
        stockOutboxRepository.save(stock);

        OrderForResponse orderResponse = new OrderForResponse(
                request.getOrderId(),
                request.getUsername(),
                request.getItemName(),
                request.getPrice(),
                request.getQuantity(),
                Status.IN_PROGRESS,
                Status.IN_PROGRESS
        );
        orderForResponseRepository.save(orderResponse);
    }
}
