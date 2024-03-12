package com.example.stockservice.consumer;

import com.example.kafka.KafkaConstants;
import com.example.kafka.dto.orchestratortostock.OrchestratorToStockRequest;
import com.example.stockservice.stock.Stock;
import com.example.stockservice.stock.StockOutbox;
import com.example.stockservice.stock.StockOutboxRepository;
import com.example.stockservice.stock.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.kafka.KafkaConstants.STOCK_CONSUMER_CONTAINER_NAME;
import static com.example.kafka.KafkaConstants.STOCK_TOPIC_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final StockRepository stockRepository;
    private final StockOutboxRepository stockOutboxRepository;

    @Transactional
    @RetryableTopic(
            kafkaTemplate = KafkaConstants.STOCK_DLQ_TEMPLATE_NAME,
            backoff = @Backoff(value = 2000L),
            dltTopicSuffix = ".dlt"
    )
    @KafkaListener(topics = STOCK_TOPIC_NAME, containerFactory = STOCK_CONSUMER_CONTAINER_NAME)
    public void listener(OrchestratorToStockRequest request) {
        log.info("Data consuming: {}", request);
        if (request == null) {
            throw new IllegalArgumentException("Request is null");
        }
        Stock stock = stockRepository.findStockByItemName(request.getItemName())
                .orElseThrow(EntityNotFoundException::new);

        stock.adjustStock(request.getQuantity());
        stockRepository.save(stock);
        stockOutboxRepository.save(new StockOutbox(request.getOrderId(), LocalDateTime.now()));
    }
}
