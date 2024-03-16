package com.example.orderservice.producer;

import com.example.kafka.KafkaConstants;
import com.example.kafka.dto.ordertoorchestrator.OrderToOrchestratorRequest;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderOutbox;
import com.example.orderservice.domain.OrderOutboxRepository;
import com.example.orderservice.domain.OrderRepository;
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
public class RelayForOrchestrator {

    private final KafkaTemplate<String, OrderToOrchestratorRequest> kafkaTemplate;
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;

    @Transactional
    @Scheduled(fixedRate = 1000L)
    public void relay() {
        List<Long> orderIds = orderOutboxRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(OrderOutbox::getSavedAt))
                .map(OrderOutbox::getOrderId)
                .toList();

        orderIds
                .forEach(orderId -> {
                    Order order = orderRepository.findById(orderId)
                            .orElseThrow(EntityNotFoundException::new);
                    kafkaTemplate.send(KafkaConstants.ORCHESTRATOR_TOPIC_NAME, new OrderToOrchestratorRequest(
                            order.getId(),
                            order.getUserName(),
                            order.getPrice(),
                            order.getItemName(),
                            order.getQuantity()));
                    orderOutboxRepository.deleteById(order.getId());
                });
    }
}
