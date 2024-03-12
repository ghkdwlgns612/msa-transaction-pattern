package com.example.orderservice.producer;

import com.example.kafka.KafkaConstants;
import com.example.kafka.dto.ordertoorchestrator.OrderToOrchestratorRequest;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderOutbox;
import com.example.orderservice.domain.OrderOutboxRepository;
import com.example.orderservice.domain.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RelayForOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
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
                    OrderToOrchestratorRequest orchestratorRequest
                            = new OrderToOrchestratorRequest();
                    kafkaTemplate.send(KafkaConstants.ORCHESTRATOR_TOPIC_NAME, orchestratorRequest);
                    orderOutboxRepository.deleteById(order.getId());
                    orderRepository.save(order);
                });
    }
}
