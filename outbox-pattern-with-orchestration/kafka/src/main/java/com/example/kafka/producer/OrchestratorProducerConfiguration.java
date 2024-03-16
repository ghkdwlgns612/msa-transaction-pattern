package com.example.kafka.producer;

import com.example.kafka.dto.orchestratortoorder.OrderResultResponse;
import com.example.kafka.dto.orchestratortopayment.OrchestratorToPaymentRequest;
import com.example.kafka.dto.orchestratortostock.OrchestratorToStockRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "kafka.configuration.orchestrator", havingValue = "true")
public class OrchestratorProducerConfiguration {

    @Value("${kafka.server.url}")
    private String kafkaUrl;

    @Bean
    public ProducerFactory<String, OrderResultResponse> successFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        return new DefaultKafkaProducerFactory<>(config, new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, OrderResultResponse> orderSuccessTopicKafkaTemplate() {
        return new KafkaTemplate<>(successFactory());
    }

    @Bean
    public ProducerFactory<String, OrchestratorToPaymentRequest> paymentFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        return new DefaultKafkaProducerFactory<>(config, new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, OrchestratorToPaymentRequest> paymentTopicKafkaTemplate() {
        return new KafkaTemplate<>(paymentFactory());
    }

    @Bean
    public ProducerFactory<String, OrchestratorToStockRequest> stockFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        return new DefaultKafkaProducerFactory<>(config, new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, OrchestratorToStockRequest> stockTopicKafkaTemplate() {
        return new KafkaTemplate<>(stockFactory());
    }

    @Bean
    public DefaultKafkaProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
