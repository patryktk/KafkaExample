package pl.tkaczyk.productsmicroservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import pl.tkaczyk.core.ProductCreatedEvent;
import pl.tkaczyk.productsmicroservice.rest.CreateProductRestModel;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;


    @Override
    public String createProduct(CreateProductRestModel product) throws Exception{
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                product.getTitle(),
                product.getPrice(),
                product.getQuantity());

        ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord<>(
                "product-created-events-topic",
                productId,
                productCreatedEvent
        );
        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, ProductCreatedEvent> result = kafkaTemplate.send(record).get();
        log.info("Partition: " + result.getProducerRecord().partition());
        log.info("Offset: " + result.getRecordMetadata().offset());
        log.info("Topic: " + result.getProducerRecord().topic());

        log.info("Returning , {}", productId);

        return productId;
    }
}
