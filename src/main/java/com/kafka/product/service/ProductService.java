package com.kafka.product.service;

import com.kafka.product.dto.ProductEventDTO;
import com.kafka.product.entity.Product;
import com.kafka.product.kafka.ProductProducer;
import com.kafka.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductProducer productProducer;
    
    @Transactional
    public Product createProduct(Product product) {
        log.info("Creating product: {}", product);
        
        // Save product to database
        Product savedProduct = productRepository.save(product);
        log.info("Product saved to database with id: {}", savedProduct.getId());
        
        // Convert to ProductEventDTO and send to Kafka
        ProductEventDTO productEvent = new ProductEventDTO(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice(),
                savedProduct.getQuantity()
        );
        
        productProducer.sendProductEvent(productEvent);
        
        return savedProduct;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
}
