package com.example.stockservice;

import com.example.stockservice.stock.Stock;
import com.example.stockservice.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class StockServiceApplication implements CommandLineRunner {

    private final StockRepository stockRepository;

    public static void main(String[] args) {
        SpringApplication.run(StockServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        List<Stock> stocks = List.of(
                new Stock("computer", 15),
                new Stock("pencil", 10),
                new Stock("keyboard", 7),
                new Stock("monitor", 5),
                new Stock("phone", 3)
        );

        stockRepository.saveAll(stocks);
    }
}
