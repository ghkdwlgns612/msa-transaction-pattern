package com.example.orderservice.consumer;

import com.example.dto.ordercommon.OrderSuccessResponse;
import com.example.dto.ordertopayment.PaymentCompensationRequest;
import com.example.dto.ordertostock.OrderToStockRequest;
import com.example.dto.ordertostock.StockCompensationRequest;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.KafkaConstants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;

    @Transactional
    @KafkaListener(topics = STOCK_SUCCESS_TOPIC_NAME, containerFactory = ORDER_STOCK_CONSUMER_CONTAINER_NAME)
    public void listenSuccess(OrderSuccessResponse response) {
        log.info("Succeeded Data consuming: {}", response);
        if (response == null) {
            throw new IllegalArgumentException("Response is null");
        }

        Order order = orderRepository.findById(response.getOrderId())
                .orElseThrow(EntityNotFoundException::new);
        order.completeStock();

        // self compensation
        if (order.isFailed()) {
            kafkaTemplate.send(
                    STOCK_COMPENSATION_TOPIC_NAME,
                    new StockCompensationRequest(order.getItemName(), order.getQuantity()));
            orderRepository.save(order);
            return;
        }

        if (order.isCompleteLinkedServices()) {
            order.completed();
        }
        orderRepository.save(order);
    }

    @Transactional
    @KafkaListener(topics = STOCK_DLQ_TOPIC_NAME, containerFactory = ORDER_STOCK_CONSUMER_CONTAINER_NAME)
    public void listenFail(OrderToStockRequest request) {
        log.info("Failed Data consuming: {}", request);
        if (request == null) {
            throw new IllegalArgumentException("Response is null");
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(EntityNotFoundException::new);
        order.failOrderByStock();
        orderRepository.save(order);

        if (order.isPaymentSucceeded()) {
            kafkaTemplate.send(
                    PAYMENT_COMPENSATION_TOPIC_NAME,
                    new PaymentCompensationRequest(order.getUserName(), order.getPrice()));
        }
    }
}
