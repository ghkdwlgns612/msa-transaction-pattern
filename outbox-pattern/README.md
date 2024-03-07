# Outbox pattern + Saga pattern(Choreography)

## 1. Architecture

![](image/transaction-outbox.jpg)

### 1-1. Transaction

![](image/trasaction-compensation.jpg)

## 2. Install(zookeeper, kafka)

1. Download kafka

https://kafka.apache.org/downloads

2. Start Zookeeper
```
bin/zookeeper-server-start.sh config/zookeeper.properties
```

3. Start Kafka
```
bin/kafka-server-start.sh config/server.properties
``` 

4. Create Topic
```
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic payment
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic payment.success
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic payment.dlt
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic payment.compensation

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic stock
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic stock.success
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic stock.dlt
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic stock.compensation

// check topic
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

5. Create Database

```
// Use same DB Instance.
create database orderdb;
create database stockdb;
create database paymentdb;
```

6. Start Spring boot
```
order-service(8080)
stock-service(8081)
payment-service(8082)
```

## 3. Concern

### 1. What if I succeeded in publishing on Payment Topic but failed to publishing on Stock Topic?

answer: You need to roll back the message that went into the Payment Topic. And the data in the order_outbox table data should not be deleted. So I used "kafkaTemplate.executeInTransaction".

### 2. If multiple Springboot instances run a schedule, at some point two instances can send messages at the same time. What should I do in this case?

answer1: Use the leader selection feature to modify only one instance to be scheduled.

answer2: Add validation logic in the consumer(payment-service, stock-service), which conferences the published message.

### 3. How do I guarantee publish producer message?

answer: Record the storage time in the order_outbox table and publish it in order.

### 4. What if there is an exception in payment, stock service?

answer1: Blocking strategy

answer1: Non-Blocking strategy(DLQ)

#### 4-1. What actions should I take in Order Service?

answer: Send a compensation request to StockService.

### 5. Compensation transactions based on each service status

|order-service|payment-service|stock-service|compensation|
|------|---|---|---|
|FAILED|FAILED|SUCCESS|stock compensation|
|FAILED|FAILED|IN_PROGRESS -> SUCCESS|self compensation(stock)|
|FAILED|FAILED|IN_PROGRESS -> FAILED|X|
|FAILED|SUCCESS|FAILED|payment compensation|
|FAILED|IN_PROGRESS -> FAILED|FAILED|X|
|FAILED|IN_PROGRESS -> SUCCESS|FAILED|self compensation(payment)|
|FAILED|FAILED|FAILED|X|
|SUCCESS|SUCCESS|SUCCESS|X|
