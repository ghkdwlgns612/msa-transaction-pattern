package com.example.paymentservice.balance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceOutboxRepository extends JpaRepository<BalanceOutbox, Long> {
}
