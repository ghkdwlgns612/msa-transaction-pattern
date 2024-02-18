package com.example.orderservice;

import com.example.orderservice.order.Order;
import com.example.orderservice.order.OrderOutbox;
import com.example.orderservice.order.OrderOutboxRepository;
import com.example.orderservice.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        orderOutboxRepository.save(new OrderOutbox(savedOrder.getId()));

        return "주문 완료";
    }
}
