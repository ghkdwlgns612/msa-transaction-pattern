package com.example.stockservice.schedule;

import com.example.ordercommon.OrderSuccessResponse;
import com.example.stockservice.stock.StockOutbox;
import com.example.stockservice.stock.StockOutboxRepository;
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

    private static final String STOCK_SUCCESS_TOPIC = "stock.success";

    private final StockOutboxRepository stockOutboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    @Scheduled(fixedRate = 1000L)
    public void sendSuccessMessageToOrderService() {
        List<Long> succeededOrderIds = stockOutboxRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(StockOutbox::getSavedAt))
                .map(StockOutbox::getOrderId)
                .toList();

        succeededOrderIds
                .forEach(succeededOrderId -> {
                            kafkaTemplate.send(STOCK_SUCCESS_TOPIC, new OrderSuccessResponse(succeededOrderId));
                            stockOutboxRepository.deleteById(succeededOrderId);
                        }
                );
    }
}


