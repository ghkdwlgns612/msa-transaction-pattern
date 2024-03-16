package com.example.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static com.example.kafka.KafkaConstants.*;

@Configuration
@ConditionalOnProperty(value = "kafka.configuration.orchestrator", havingValue = "true")
public class OrchestratorConsumerConfiguration {

    @Value("${kafka.server.url}")
    private String kafkaUrl;

    @Bean
    public ConsumerFactory<String, Object> orchestratorFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "orchestrator");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean(ORCHESTRATOR_ORDER_CONSUMER_CONTAINER_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, Object> orchestratorListenerContainer() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orchestratorFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> orchestratorPaymentSuccessFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "orchestrator_payment_success");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean(ORCHESTRATOR_PAYMENT_SUCCESS_CONSUMER_CONTAINER_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, Object> orchestratorPaymentSuccessListenerContainer() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orchestratorPaymentSuccessFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> orchestratorPaymentFailFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "orchestrator_payment_fail");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean(ORCHESTRATOR_PAYMENT_FAIL_CONSUMER_CONTAINER_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, Object> orchestratorPaymentFailListenerContainer() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orchestratorPaymentFailFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> orchestratorStockSuccessFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "orchestrator_stock_success");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean(ORCHESTRATOR_STOCK_SUCCESS_CONSUMER_CONTAINER_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, Object> orchestratorStockSuccessListenerContainer() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orchestratorStockSuccessFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> orchestratorStockFailFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "orchestrator_stock_fail");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean(ORCHESTRATOR_STOCK_FAIL_CONSUMER_CONTAINER_NAME)
    public ConcurrentKafkaListenerContainerFactory<String, Object> orchestratorStockFailListenerContainer() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orchestratorStockFailFactory());
        return factory;
    }
}
