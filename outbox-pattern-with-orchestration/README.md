# Outbox pattern + Saga pattern(Orchestration)

## 1. Architecture

![](image/orchestrator.png)

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
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic orchestrator
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --topic orchestrator.response


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
create database order_orchestratordb;
create database orderdb;
create database stockdb;
create database paymentdb;
```

6. Start Spring boot
```
order-orchestrator(8080)
order-service(8081)
payment-service(8082)
stock-service(8083)
```


## 3. Concern

### 1. In what cases should I use the outbox pattern?

answer: Logic to run periodically rather than immediately responding. For example, suppose Compensation. Compensation for Payment is executed in two cases.

1. payment-service(success) -> stock-service(fail) --> payment compensation.
2. stock-service(fail) -> payment-service(success) --> payment compensation.

A duplicate message may be issued instantaneously according to a stock-service failure time point.
