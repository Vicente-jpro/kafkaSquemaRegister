# Quick Start Guide

This guide will help you quickly set up and run the Kafka Schema Registry project.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose

## Step 1: Start Infrastructure

Start all required services (Zookeeper, 3 Kafka brokers, and Schema Registry):

```bash
docker compose up -d
```

Wait about 30-60 seconds for all services to fully initialize.

Verify all services are running:

```bash
docker compose ps
```

You should see 5 running containers:
- zookeeper (port 2181)
- kafka-broker-1 (port 9092)
- kafka-broker-2 (port 9093)
- kafka-broker-3 (port 9094)
- schema-registry (port 8081)

## Step 2: Build the Application

```bash
mvn clean install
```

This will:
- Download dependencies
- Compile the application
- Run tests (if any)
- Package the application

## Step 3: Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/kafka-schema-register-1.0.0.jar
```

Wait for the application to start. You should see:
```
Started KafkaSchemaRegisterApplication in X seconds
```

## Step 4: Test the Application

### Option A: Use the test script

```bash
./test-api.sh
```

### Option B: Manual testing

**Create a product:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1299.99,
    "quantity": 10
  }'
```

**Get all products:**
```bash
curl http://localhost:8080/api/products
```

**Get product by ID:**
```bash
curl http://localhost:8080/api/products/1
```

## Step 5: Verify Kafka Messages

Check the application logs to see:
1. Product saved to database
2. Message sent to Kafka topic
3. Consumer received and processed the message

Example log output:
```
INFO c.kafka.product.service.ProductService   : Product saved to database with id: 1
INFO c.kafka.product.kafka.ProductProducer    : Sending product event to topic: product-create-quee
INFO c.kafka.product.kafka.ProductConsumer    : Received product event from topic: product-create-quee
INFO c.kafka.product.kafka.ProductConsumer    : Product event processed successfully
```

## Access H2 Console

Open: http://localhost:8080/h2-console

**Connection details:**
- JDBC URL: `jdbc:h2:mem:productdb`
- Username: `sa`
- Password: (empty)

Query products:
```sql
SELECT * FROM products;
```

## Stopping the Application

1. Stop Spring Boot: Press `Ctrl+C` in the terminal
2. Stop Docker services:
```bash
docker compose down
```

To remove all data:
```bash
docker compose down -v
```

## Troubleshooting

**Issue: Port already in use**
```bash
# Find what's using the port
lsof -i :8080
lsof -i :9092

# Kill the process or change the port in configuration
```

**Issue: Kafka connection refused**
- Wait 30-60 seconds for Kafka to fully start
- Check Docker logs: `docker compose logs kafka-broker-1`

**Issue: Application fails to start**
- Ensure Java 17 is installed: `java -version`
- Ensure all Docker containers are running: `docker compose ps`

## Architecture Verification

The complete flow works as follows:

1. **User** → POST /api/products → **REST Controller**
2. **Controller** → **Service Layer**
3. **Service** → Saves to **H2 Database**
4. **Service** → Sends message to **Kafka Producer**
5. **Producer** → Publishes to topic **"product-create-quee"**
6. **Kafka Broker** → Stores the message
7. **Consumer** → Receives message from topic
8. **Consumer** → Processes message (with 3 retry attempts if fails)

## Next Steps

- Check the main README.md for detailed documentation
- Review the code in `src/main/java/com/kafka/product/`
- Experiment with error scenarios to test the retry policy
- Monitor Kafka topics using Docker exec commands

## Support

For issues or questions, refer to the main README.md file.
