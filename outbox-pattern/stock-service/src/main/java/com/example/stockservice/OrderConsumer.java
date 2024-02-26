package com.example.stockservice;

import com.example.ordertostock.OrderToStockRequest;
import com.example.stockservice.stock.Stock;
import com.example.stockservice.stock.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final StockRepository stockRepository;

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
    }
}
