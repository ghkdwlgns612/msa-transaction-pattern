package com.example.orderservice.consumer;

import com.example.ordercommon.OrderSuccessResponse;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderRepository;
import com.example.ordertopayment.OrderToPaymentRequest;
import com.example.ordertopayment.PaymentCompensationRequest;
import com.example.ordertostock.StockCompensationRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {

    private static final String PAYMENT_COMPENSATION_TOPIC = "payment.compensation";
    private static final String STOCK_COMPENSATION_TOPIC = "stock.compensation";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;

    @Transactional
    @KafkaListener(topics = "payment.success", containerFactory = "paymentListenerContainer")
    public void listenSuccess(OrderSuccessResponse response) {
        log.info("Succeeded Data consuming: {}", response);
        if (response == null) {
            throw new IllegalArgumentException("Response is null");
        }

        Order order = orderRepository.findById(response.getOrderId())
                .orElseThrow(EntityNotFoundException::new);
        order.completePayment();

        // self compensation
        if (order.isFailed()) {
            kafkaTemplate.send(
                    PAYMENT_COMPENSATION_TOPIC,
                    new PaymentCompensationRequest(order.getUserName(), order.getPrice()));
            orderRepository.save(order);
            return;
        }

        if (order.isCompleteLinkedServices()) {
            order.completed();
        }
        orderRepository.save(order);
    }

    @Transactional
    @KafkaListener(topics = "payment.dlt", containerFactory = "paymentListenerContainer")
    public void listenFail(OrderToPaymentRequest request) {
        log.info("Failed Data consuming: {}", request);
        if (request == null) {
            throw new IllegalArgumentException("Response is null");
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(EntityNotFoundException::new);
        order.failOrderByPayment();
        orderRepository.save(order);

        if (order.isStockSucceeded()) {
            kafkaTemplate.send(
                    STOCK_COMPENSATION_TOPIC,
                    new StockCompensationRequest(order.getItemName(), order.getQuantity()));
        }
    }
}
