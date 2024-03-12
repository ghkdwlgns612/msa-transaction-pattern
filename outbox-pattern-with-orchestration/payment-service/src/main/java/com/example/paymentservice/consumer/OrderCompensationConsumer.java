package com.example.paymentservice.consumer;

import com.example.kafka.dto.orchestratortopayment.PaymentCompensationRequest;
import com.example.paymentservice.balance.Balance;
import com.example.paymentservice.balance.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.kafka.KafkaConstants.PAYMENT_COMPENSATION_TOPIC_NAME;
import static com.example.kafka.KafkaConstants.PAYMENT_CONSUMER_COMPENSATION_CONTAINER_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCompensationConsumer {

    private final BalanceRepository balanceRepository;

    @Transactional
    @KafkaListener(topics = PAYMENT_COMPENSATION_TOPIC_NAME,
            containerFactory = PAYMENT_CONSUMER_COMPENSATION_CONTAINER_NAME)
    public void listener(PaymentCompensationRequest request) {
        log.info("Data consuming: {}", request);
        if (request == null) {
            throw new IllegalArgumentException("Request is null");
        }

        Balance balance = balanceRepository.findBalanceByUserName(request.getUsername())
                .orElseThrow(EntityNotFoundException::new);

        balance.balance(request.getPrice() * -1);
        balanceRepository.save(balance);
    }
}
