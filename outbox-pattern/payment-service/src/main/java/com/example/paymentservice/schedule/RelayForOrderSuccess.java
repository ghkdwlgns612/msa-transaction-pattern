package com.example.paymentservice.schedule;

import com.example.ordercommon.OrderSuccessResponse;
import com.example.paymentservice.balance.BalanceOutbox;
import com.example.paymentservice.balance.BalanceOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RelayForOrderSuccess {

    private static final String PAYMENT_SUCCESS_TOPIC = "payment.success";

    private final BalanceOutboxRepository balanceOutboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @Scheduled(fixedRate = 1000L)
    public void sendSuccessMessageToOrderService() {
        List<Long> succeededOrderIds = balanceOutboxRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(BalanceOutbox::getSavedAt))
                .map(BalanceOutbox::getOrderId)
                .toList();

        succeededOrderIds
                .forEach(succeededOrderId -> {
                            kafkaTemplate.send(PAYMENT_SUCCESS_TOPIC, new OrderSuccessResponse(succeededOrderId));
                            balanceOutboxRepository.deleteById(succeededOrderId);
                        }
                );
    }
}
