package com.example.orderservice.schedule;

import com.example.orderservice.order.Order;
import com.example.orderservice.order.OrderOutbox;
import com.example.orderservice.order.OrderOutboxRepository;
import com.example.orderservice.order.OrderRepository;
import com.example.orderservice.schedule.request.OrderToPaymentRequest;
import com.example.orderservice.schedule.request.OrderToStockRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RelayForPaymentStock {

    private static final String PAYMENT_TOPIC = "payment";
    private static final String STOCK_TOPIC = "stock";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;

    @Transactional
    @Scheduled(fixedRate = 10000L)
    public void relay() {
        List<Long> orderIds = orderOutboxRepository.findAll()
                .stream()
                .map(OrderOutbox::getOrderId)
                .toList();

        orderIds
                .forEach(orderId -> {
                    Order order = orderRepository.findById(orderId)
                            .orElseThrow(EntityNotFoundException::new);
                    OrderToPaymentRequest paymentRequest
                            = new OrderToPaymentRequest(order.getUserName(), order.getPrice());
                    OrderToStockRequest stockRequest
                            = new OrderToStockRequest(order.getItemName(), order.getQuantity());
                    kafkaTemplate.executeInTransaction(operations -> {
                        operations.send(PAYMENT_TOPIC, paymentRequest);
                        operations.send(STOCK_TOPIC, stockRequest);
                        orderOutboxRepository.deleteById(order.getId());
                        return true;
                    });
                });
    }
}
