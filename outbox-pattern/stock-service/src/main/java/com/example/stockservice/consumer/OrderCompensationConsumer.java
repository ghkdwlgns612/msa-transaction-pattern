package com.example.stockservice.consumer;

import com.example.ordertopayment.PaymentCompensationRequest;
import com.example.ordertostock.StockCompensationRequest;
import com.example.stockservice.stock.Stock;
import com.example.stockservice.stock.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCompensationConsumer {

    private final StockRepository stockRepository;

    @Transactional
    @KafkaListener(topics = "stock.compensation", containerFactory = "compensationListenerContainer")
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
