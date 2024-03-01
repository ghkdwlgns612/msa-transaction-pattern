package com.example.orderservice.consumer;

import com.example.ordercommon.OrderSuccessResponse;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "stock.success", containerFactory = "stockListenerContainer")
    public void listen(OrderSuccessResponse response) {
        log.info("Data consuming: {}", response);
        if (response == null) {
            throw new IllegalArgumentException("Response is null");
        }

        Order order = orderRepository.findById(response.getOrderId())
                .orElseThrow(EntityNotFoundException::new);

        if (order.isOrdering()) {
            order.completed();
            orderRepository.save(order);
        }
    }
}
