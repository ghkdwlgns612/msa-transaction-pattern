package com.example.paymentservice;

import com.example.ordertopayment.OrderToPaymentRequest;
import com.example.paymentservice.balance.Balance;
import com.example.paymentservice.balance.BalanceOutbox;
import com.example.paymentservice.balance.BalanceOutboxRepository;
import com.example.paymentservice.balance.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
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
    @RetryableTopic(kafkaTemplate = "retryableTopicKafkaTemplate", backoff = @Backoff(value = 2000L))
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

    @DltHandler
    public void handleDltPayment(OrderToPaymentRequest request) {
        // 1. Payment Service 에러발생
        // 2. payment-error 큐로 이벤트 발행
        // 3. Order Service 에서 Consume
        // 4. OrderService 의 Order 상태가 FAILED 인지 확인(왜냐하면 Stock에서 실패할 수 있으니)
        // 4-1. FAILED 라면 Stock Service 에서 이미 실패한 것이기에 "잔고 증가 메세지" 발행 불필요.
        // 4-1-1. Order Service 및 Stock Service 아무 처리하지 않음.
        // 4-2. IN_PROGRESS 라면 Stock Service 에 대한 보상 트랜잭션도 필요.
        // 4-2-1. Stock Service 가 완료되었다면
        // 4-2-2. Stock Service 가 진행 중 이라면 어떻게 해야하지?? while 문으로 계속 돌려야하나?
        log.info("Error Data : {}", request.toString());
    }
}
