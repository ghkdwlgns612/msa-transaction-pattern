package com.example.paymentservice;

import com.example.paymentservice.payment.Balance;
import com.example.paymentservice.payment.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class PaymentServiceApplication implements CommandLineRunner {

    private final BalanceRepository balanceRepository;

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        List<Balance> balances = List.of(
                new Balance("martini", 100_000),
                new Balance("jake", 50_000),
                new Balance("helen", 30_000),
                new Balance("oskar", 20_000),
                new Balance("liam", 10_000)
        );
        balanceRepository.saveAll(balances);
    }
}
