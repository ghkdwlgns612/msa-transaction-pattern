package com.example.paymentservice.consumer;

import com.example.dto.ordertopayment.OrderToPaymentRequest;
import com.example.paymentservice.balance.Balance;
import com.example.paymentservice.balance.BalanceOutbox;
import com.example.paymentservice.balance.BalanceOutboxRepository;
import com.example.paymentservice.balance.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final BalanceRepository balanceRepository;
    private final BalanceOutboxRepository balanceOutboxRepository;

    @Transactional
    @RetryableTopic(
            kafkaTemplate = "retryableTopicKafkaTemplate",
            backoff = @Backoff(value = 2000L),
            dltTopicSuffix = ".dlt"
    )
    @KafkaListener(topics = "payment", containerFactory = "kafkaListenerContainer")
    public void listener(OrderToPaymentRequest request) {
        log.info("Data consuming: {}", request);
        if (request == null) {
            throw new IllegalArgumentException("Request is null");
        }

        Balance balance = balanceRepository.findBalanceByUserName(request.getUsername())
                .orElseThrow(EntityNotFoundException::new);

        balance.balance(request.getPrice());
        balanceRepository.save(balance);
        balanceOutboxRepository.save(new BalanceOutbox(request.getOrderId(), LocalDateTime.now()));
    }
}
