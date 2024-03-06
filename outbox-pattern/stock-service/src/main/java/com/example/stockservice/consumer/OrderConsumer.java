package com.example.stockservice.consumer;

import com.example.dto.ordertostock.OrderToStockRequest;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final StockRepository stockRepository;
    private final StockOutboxRepository stockOutboxRepository;

    @Transactional
    @RetryableTopic(
            kafkaTemplate = "retryableTopicKafkaTemplate",
            backoff = @Backoff(value = 2000L),
            dltTopicSuffix = ".dlt"
    )
    @KafkaListener(topics = "stock", containerFactory = "kafkaListenerContainer")
    public void listener(OrderToStockRequest request) {
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
