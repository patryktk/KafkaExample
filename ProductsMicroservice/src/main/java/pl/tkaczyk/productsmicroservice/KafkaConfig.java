package pl.tkaczyk.productsmicroservice;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import pl.tkaczyk.productsmicroservice.service.ProductCreatedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaEnvObject kafkaEnvObject;


    Map<String, Object> producerConfigs() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEnvObject.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaEnvObject.getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaEnvObject.getValueSerializer());
        config.put(ProducerConfig.ACKS_CONFIG, kafkaEnvObject.getAcks());
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, kafkaEnvObject.getProperties().get("timeout"));
        config.put(ProducerConfig.LINGER_MS_CONFIG, kafkaEnvObject.getProperties().get("linger"));
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaEnvObject.getProperties().get("request"));

        return config;
    }

    @Bean
    ProducerFactory<String, ProductCreatedEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<String, ProductCreatedEvent>(producerFactory());
    }


    @Bean
    NewTopic createNewTopic() {
        return TopicBuilder.name("product-created-events-topic")
                .partitions(3)
                .replicas(3)
                .configs(Map.of(
                        "min.insync.replicas",
                        "2")) //minimalna ilość replik, które muszą potwierdzić, że udało się zapisać ten topic. Jeżeli nie będzie potwierdzenia to excpetion. Ale to będzie wolniejsze, ale większa pewność, nie tracimy danych
                .build();
    }


}
