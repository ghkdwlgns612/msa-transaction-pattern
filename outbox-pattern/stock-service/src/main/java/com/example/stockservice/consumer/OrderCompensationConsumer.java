package com.example.stockservice.consumer;

import com.example.dto.ordertostock.StockCompensationRequest;
import com.example.stockservice.stock.Stock;
import com.example.stockservice.stock.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.KafkaConstants.STOCK_COMPENSATION_TOPIC_NAME;
import static com.example.KafkaConstants.STOCK_CONSUMER_COMPENSATION_CONTAINER_NAME;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCompensationConsumer {

    private final StockRepository stockRepository;

    @Transactional
    @KafkaListener(
            topics = STOCK_COMPENSATION_TOPIC_NAME,
            containerFactory = STOCK_CONSUMER_COMPENSATION_CONTAINER_NAME)
    public void listener(StockCompensationRequest request) {
        log.info("Data consuming: {}", request);
        if (request == null) {
            throw new IllegalArgumentException("Request is null");
        }

        Stock stock = stockRepository.findStockByItemName(request.getItemName())
                .orElseThrow(EntityNotFoundException::new);

        stock.adjustStock(request.getQuantity() * -1);
        stockRepository.save(stock);
    }
}
