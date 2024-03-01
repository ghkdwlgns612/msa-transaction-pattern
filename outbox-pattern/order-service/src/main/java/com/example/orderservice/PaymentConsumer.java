package com.example.orderservice;

import com.example.ordercommon.OrderSuccessResponse;
import com.example.orderservice.order.Order;
import com.example.orderservice.order.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment.success", containerFactory = "paymentListenerContainer")
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
