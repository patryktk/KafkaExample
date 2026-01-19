package pl.tkaczyk.productsmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;

@SpringBootApplication
@EnableConfigurationProperties(KafkaEnvObject.class)
public class ProductsMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductsMicroserviceApplication.class, args);
    }

}
