package com.example.orderorchestrator.order.consumer.stock;

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

import static com.example.kafka.KafkaConstants.ORCHESTRATOR_STOCK_SUCCESS_CONSUMER_CONTAINER_NAME;
import static com.example.kafka.KafkaConstants.STOCK_SUCCESS_TOPIC_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockSuccessConsumer {

    private final OrderForResponseRepository orderForResponseRepository;
    private final KafkaTemplate<String, OrderResultResponse> kafkaTemplate;

    @Transactional
    @KafkaListener(
            topics = STOCK_SUCCESS_TOPIC_NAME,
            containerFactory = ORCHESTRATOR_STOCK_SUCCESS_CONSUMER_CONTAINER_NAME)
    void listen(OrderSuccessResponse response) {
        log.info("Success stock: {}", response);
        OrderForResponse order = orderForResponseRepository.findById(response.getOrderId())
                .orElseThrow(EntityNotFoundException::new);

        order.successStock();
        if (order.isSucceedPayment()) {
            log.info("Send success data to order-service: {}", order);
            kafkaTemplate.send(
                    KafkaConstants.ORCHESTRATOR_ORDER_RESPONSE_TOPIC_NAME,
                    new OrderResultResponse(order.getOrderId(), true));
        }

        orderForResponseRepository.save(order);
    }
}
