package pl.tkaczyk.productsmicroservice;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EmbeddedKafka
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.kafka.producer.bootstrap-servers}")
public class ProductServiceIntegrationTest {
}
