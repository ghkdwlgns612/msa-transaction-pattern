package com.example.orderorchestrator.order.producer.stock;

import com.example.kafka.KafkaConstants;
import com.example.kafka.dto.orchestratortostock.OrchestratorToStockRequest;
import com.example.orderorchestrator.order.orchestrator.OrderForResponse;
import com.example.orderorchestrator.order.orchestrator.OrderForResponseRepository;
import com.example.orderorchestrator.order.orchestrator.StockOutbox;
import com.example.orderorchestrator.order.orchestrator.StockOutboxRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RelayForStock {

    private final KafkaTemplate<String, OrchestratorToStockRequest> kafkaTemplate;
    private final StockOutboxRepository stockOutboxRepository;
    private final OrderForResponseRepository orderForResponseRepository;

    @Transactional
    @Scheduled(fixedRate = 1000L)
    public void requestStock() {
        List<Long> orderIds = stockOutboxRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(StockOutbox::getSavedAt))
                .map(StockOutbox::getOrderId)
                .toList();

        orderIds
                .forEach(orderId -> {
                    OrderForResponse order = orderForResponseRepository.findById(orderId)
                            .orElseThrow(EntityNotFoundException::new);
                    kafkaTemplate.send(KafkaConstants.STOCK_TOPIC_NAME,
                            new OrchestratorToStockRequest(orderId, order.getItemName(), order.getQuantity()));
                    stockOutboxRepository.deleteById(orderId);
                });
    }
}
