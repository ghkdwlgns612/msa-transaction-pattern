package com.example.paymentservice;

import com.example.ordertopayment.OrderToPaymentRequest;
import com.example.paymentservice.balance.Balance;
import com.example.paymentservice.balance.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final BalanceRepository balanceRepository;

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
    }
}
