package com.example.orderservice.producer;

import com.example.dto.ordertopayment.OrderToPaymentRequest;
import com.example.dto.ordertostock.OrderToStockRequest;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderOutbox;
import com.example.orderservice.domain.OrderOutboxRepository;
import com.example.orderservice.domain.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static com.example.KafkaConstants.PAYMENT_TOPIC_NAME;
import static com.example.KafkaConstants.STOCK_TOPIC_NAME;

@Component
@RequiredArgsConstructor
public class RelayForPaymentStock {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;

    @Transactional
    @Scheduled(fixedRate = 1000L)
    public void relay() {
        List<Long> orderIds = orderOutboxRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(OrderOutbox::getSavedAt))
                .map(OrderOutbox::getOrderId)
                .toList();

        orderIds
                .forEach(orderId -> {
                    Order order = orderRepository.findById(orderId)
                            .orElseThrow(EntityNotFoundException::new);
                    OrderToPaymentRequest paymentRequest
                            = new OrderToPaymentRequest(orderId, order.getUserName(), order.getPrice());
                    OrderToStockRequest stockRequest
                            = new OrderToStockRequest(orderId, order.getItemName(), order.getQuantity());
                    kafkaTemplate.executeInTransaction(operations -> {
                        operations.send(PAYMENT_TOPIC_NAME, paymentRequest);
                        operations.send(STOCK_TOPIC_NAME, stockRequest);
                        order.linkOtherServices();
                        orderOutboxRepository.deleteById(order.getId());
                        orderRepository.save(order);
                        return true;
                    });
                });
    }
}
