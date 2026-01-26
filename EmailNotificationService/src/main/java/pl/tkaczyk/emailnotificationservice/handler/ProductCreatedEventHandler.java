package pl.tkaczyk.emailnotificationservice.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import pl.tkaczyk.core.ProductCreatedEvent;
import pl.tkaczyk.emailnotificationservice.error.NotRetryableException;
import pl.tkaczyk.emailnotificationservice.error.RetryableException;

@Component
@KafkaListener(topics = "product-created-events-topic")
@Slf4j
@RequiredArgsConstructor
public class ProductCreatedEventHandler {

    private final RestTemplate restTemplate;

    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent) {
//        if(true) throw new NotRetryableException("An error took place. No need to consume this message again.");

        log.info("Received message: " + productCreatedEvent.getTitle());

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
    }
}
