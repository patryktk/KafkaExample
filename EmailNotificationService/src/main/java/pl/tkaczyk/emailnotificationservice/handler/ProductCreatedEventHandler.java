package pl.tkaczyk.emailnotificationservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.tkaczyk.emailnotificationservice.error.NotRetryableException;
import pl.tkaczyk.emailnotificationservice.error.RetryableException;
import pl.tkaczyk.emailnotificationservice.io.ProcessedEventEntity;
import pl.tkaczyk.emailnotificationservice.io.ProcessedEventRepository;

@Component
@KafkaListener(topics = "product-created-events-topic")
@Slf4j
@RequiredArgsConstructor
public class ProductCreatedEventHandler {

    private final RestTemplate restTemplate;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaHandler
    @Transactional
    public void handle(
            @Payload ProductCreatedEvent productCreatedEvent,
            @Header(value = "messageId") String messageId,
            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey
    ) {
        log.info("Received message: " + productCreatedEvent.getTitle() + " with productId " + productCreatedEvent.getProductId());

        ProcessedEventEntity existingRecord = processedEventRepository.findByMessageId(messageId);

        if (existingRecord != null) {
            log.info("Message with messageId " + messageId + " already processed.");
            return;
        }

        String requestUrl = "http://localhost:8082/response/200";
        try {
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Recevied response from a remote service: " + response.getBody());
            }
        } catch (ResourceAccessException ex) {
            log.error("Resource access exception: " + ex.getMessage());
            throw new RetryableException(ex);
        } catch (HttpServerErrorException e) {
            log.error("Http Server Error: " + e.getMessage());
            throw new NotRetryableException(e);
        } catch (Exception e) {
            log.error("Unknown exception: " + e.getMessage());
            throw new NotRetryableException(e);
        }

        try {
            processedEventRepository.save(new ProcessedEventEntity(messageId, productCreatedEvent.getProductId()));
        } catch (DataIntegrityViolationException ex) {
            throw new NotRetryableException(ex);
        }
    }
}
