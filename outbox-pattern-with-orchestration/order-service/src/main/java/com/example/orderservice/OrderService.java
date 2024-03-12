package com.example.orderservice;

import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderOutbox;
import com.example.orderservice.domain.OrderOutboxRepository;
import com.example.orderservice.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;

    @Transactional
    public String orderItem(OrderDto orderDto) {
        Order order = Order.createOrder(
                orderDto.getUserName(),
                orderDto.getItemName(),
                orderDto.getPrice(),
                orderDto.getQuantity()
        );

        Order savedOrder = orderRepository.save(order);
        orderOutboxRepository.save(new OrderOutbox(savedOrder.getId(), LocalDateTime.now()));

        return "주문 완료";
    }
}
