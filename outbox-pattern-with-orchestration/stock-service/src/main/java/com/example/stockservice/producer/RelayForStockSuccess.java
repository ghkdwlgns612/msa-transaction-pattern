package com.example.stockservice.producer;

import com.example.kafka.KafkaConstants;
import com.example.kafka.dto.common.OrderSuccessResponse;
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
public class RelayForStockSuccess {

    private final StockOutboxRepository stockOutboxRepository;
    private final KafkaTemplate<String, OrderSuccessResponse> kafkaTemplate;

    @Transactional
    @Scheduled(fixedRate = 1000L)
    public void sendSuccessMessageToOrchestratorService() {
        List<Long> succeededOrderIds = stockOutboxRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(StockOutbox::getSavedAt))
                .map(StockOutbox::getOrderId)
                .toList();

        succeededOrderIds
                .forEach(succeededOrderId -> {
                            kafkaTemplate.send(KafkaConstants.STOCK_SUCCESS_TOPIC_NAME,
                                    new OrderSuccessResponse(succeededOrderId));
                            stockOutboxRepository.deleteById(succeededOrderId);
                        }
                );
    }
}
