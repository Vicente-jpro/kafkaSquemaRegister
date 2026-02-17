package com.kafka.product.kafka;

import com.kafka.product.dto.ProductEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductConsumer {
    
    @KafkaListener(topics = "product-create-quee", groupId = "product-consumer-group")
    public void consumeProductEvent(
            @Payload ProductEventDTO productEvent,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received product event from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        log.info("Product event data: {}", productEvent);
        
        try {
            // Process the product event
            processProductEvent(productEvent);
            log.info("Product event processed successfully");
        } catch (Exception e) {
            log.error("Error processing product event: {}", e.getMessage(), e);
            throw e; // Throw to trigger retry
        }
    }
    
    private void processProductEvent(ProductEventDTO productEvent) {
        // Business logic to process the product event
        log.info("Processing product: id={}, name={}, price={}, quantity={}",
                productEvent.getId(), productEvent.getName(), 
                productEvent.getPrice(), productEvent.getQuantity());
    }
}
