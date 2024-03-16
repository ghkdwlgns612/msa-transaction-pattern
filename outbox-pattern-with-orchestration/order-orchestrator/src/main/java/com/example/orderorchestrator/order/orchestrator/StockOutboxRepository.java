package com.example.orderorchestrator.order.orchestrator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockOutboxRepository extends JpaRepository<StockOutbox, Long> {
}
