package com.kafka.product.kafka;

import com.kafka.product.dto.ProductEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductProducer {
    
    private final KafkaTemplate<String, ProductEventDTO> kafkaTemplate;
    
    private static final String TOPIC = "product-create-queue";
    
    public void sendProductEvent(ProductEventDTO productEvent) {
        log.info("Sending product event to topic: {}, event: {}", TOPIC, productEvent);
        kafkaTemplate.send(TOPIC, productEvent.getId() != null ? productEvent.getId().toString() : null, productEvent);
        log.info("Product event sent successfully");
    }
}
