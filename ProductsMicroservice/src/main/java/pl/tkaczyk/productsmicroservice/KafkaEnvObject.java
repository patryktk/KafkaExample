package pl.tkaczyk.productsmicroservice;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.kafka.producer")
@Data
public class KafkaEnvObject {

    private String bootstrapServers;
    private String keySerializer;
    private String valueSerializer;
    private String acks;

    private Map<String, Object> properties = new HashMap<>();
}
