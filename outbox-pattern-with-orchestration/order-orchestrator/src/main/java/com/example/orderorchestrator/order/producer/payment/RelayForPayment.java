package com.example.orderorchestrator.order.producer.payment;

import com.example.kafka.KafkaConstants;
import com.example.kafka.dto.orchestratortopayment.OrchestratorToPaymentRequest;
import com.example.orderorchestrator.order.orchestrator.OrderForResponse;
import com.example.orderorchestrator.order.orchestrator.OrderForResponseRepository;
import com.example.orderorchestrator.order.orchestrator.PaymentOutbox;
import com.example.orderorchestrator.order.orchestrator.PaymentOutboxRepository;
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
public class RelayForPayment {

    private final KafkaTemplate<String, OrchestratorToPaymentRequest> kafkaTemplate;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final OrderForResponseRepository orderForResponseRepository;

    @Transactional
    @Scheduled(fixedRate = 1000L)
    public void requestPayment() {
        List<Long> orderIds = paymentOutboxRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(PaymentOutbox::getSavedAt))
                .map(PaymentOutbox::getOrderId)
                .toList();

        orderIds
                .forEach(orderId -> {
                    OrderForResponse order = orderForResponseRepository.findById(orderId)
                            .orElseThrow(EntityNotFoundException::new);
                    kafkaTemplate.send(KafkaConstants.PAYMENT_TOPIC_NAME,
                            new OrchestratorToPaymentRequest(orderId, order.getUserName(), order.getPrice()));
                    paymentOutboxRepository.deleteById(orderId);
                });
    }
}
