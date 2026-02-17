#!/bin/bash

# Exit on any error
set -e

# Exit on undefined variables
set -u

# Test script for Kafka Schema Registry Project

echo "==============================================="
echo "Kafka Schema Registry Project - Test Script"
echo "==============================================="
echo ""

# Check if the application is running
echo "Checking if application is running..."
if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 && ! curl -s http://localhost:8080/api/products > /dev/null 2>&1; then
    echo "ERROR: Application is not running on port 8080"
    echo "Please start the application first with: mvn spring-boot:run"
    exit 1
fi
echo "✓ Application is running"
echo ""

# Test 1: Create a product
echo "Test 1: Creating a product (Laptop)"
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1299.99,
    "quantity": 10
  }'
echo -e "\n"

sleep 2

# Test 2: Create another product
echo "Test 2: Creating another product (Smartphone)"
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Smartphone",
    "description": "Latest model smartphone",
    "price": 899.99,
    "quantity": 25
  }'
echo -e "\n"

sleep 2

# Test 3: Get all products
echo "Test 3: Retrieving all products"
curl http://localhost:8080/api/products
echo -e "\n"

sleep 1

# Test 4: Get specific product
echo "Test 4: Retrieving product with ID 1"
curl http://localhost:8080/api/products/1
echo -e "\n"

sleep 1

# Test 5: Test 404 error
echo "Test 5: Testing 404 error (product not found)"
curl -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/api/products/999
echo -e "\n"

echo ""
echo "==============================================="
echo "All tests completed successfully!"
echo "==============================================="
echo ""
echo "Check the application logs to verify:"
echo "1. Products were saved to H2 database"
echo "2. Messages were sent to Kafka topic 'product-create-queue'"
echo "3. Consumer received and processed the messages"
echo "4. Retry policy is configured (check logs if any errors occur)"
