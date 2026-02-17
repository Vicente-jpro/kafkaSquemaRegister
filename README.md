# Kafka Schema Registry Project

A Spring Boot application that demonstrates Kafka message production and consumption with Confluent Schema Registry, using Avro serialization and H2 database.

## Features

- **Spring Boot 3.1.5** with Java 17
- **Apache Kafka** with 3 brokers for high availability
- **Confluent Schema Registry** for Avro schema management
- **H2 In-Memory Database** for product storage
- **Product Management** - Create, retrieve products via REST API
- **Kafka Producer/Consumer** - Automatic message production and consumption
- **Retry Policy** - 3 retry attempts (4 total) with 1-second intervals
- **Docker Compose** - Complete infrastructure setup

## Architecture

```
User -> REST API -> Service Layer -> Database (H2)
                         |
                         v
                  Kafka Producer -> Topic: product-create-queue
                                           |
                                           v
                                    Kafka Consumer -> Process Message
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose

## Project Structure

```
kafka-schema-register/
├── src/main/java/com/kafka/product/
│   ├── KafkaSchemaRegisterApplication.java  # Main application
│   ├── entity/
│   │   └── Product.java                      # Product JPA entity
│   ├── repository/
│   │   └── ProductRepository.java            # JPA repository
│   ├── service/
│   │   └── ProductService.java               # Business logic
│   ├── controller/
│   │   └── ProductController.java            # REST endpoints
│   ├── kafka/
│   │   ├── ProductProducer.java              # Kafka producer
│   │   └── ProductConsumer.java              # Kafka consumer with retry
│   └── config/
│       └── KafkaConfig.java                  # Kafka configuration
├── src/main/resources/
│   ├── avro/
│   │   └── product-event.avsc                # Avro schema
│   └── application.properties                # Application config
├── docker-compose.yml                         # Infrastructure setup
└── pom.xml                                    # Maven dependencies
```

## Getting Started

### 1. Start Kafka Infrastructure

Start Zookeeper, 3 Kafka brokers, and Schema Registry using Docker Compose:

```bash
docker-compose up -d
```

Verify all services are running:

```bash
docker-compose ps
```

You should see:
- zookeeper (port 2181)
- kafka-broker-1 (port 9092)
- kafka-broker-2 (port 9093)
- kafka-broker-3 (port 9094)
- schema-registry (port 8081)

### 2. Build the Application

Generate Avro classes and build the project:

```bash
mvn clean install
```

This will:
- Generate Java classes from Avro schema
- Compile the application
- Run tests

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR:

```bash
java -jar target/kafka-schema-register-1.0.0.jar
```

The application will start on port 8080.

## API Endpoints

### Create Product

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

Response:
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 1299.99,
  "quantity": 10
}
```

### Get All Products

```bash
curl http://localhost:8080/api/products
```

### Get Product by ID

```bash
curl http://localhost:8080/api/products/1
```

## How It Works

1. **User creates a product** via REST API (`POST /api/products`)
2. **Service layer** saves the product to H2 database
3. **Service layer** converts the product to ProductEvent (Avro)
4. **Kafka Producer** sends the event to `product-create-queue` topic
5. **Schema Registry** validates and stores the Avro schema
6. **Kafka Consumer** receives the message from the topic
7. **Retry Policy** - If processing fails, it retries up to 3 times with 1-second intervals
8. **Consumer** processes the event and logs the information

## Kafka Configuration

### Topics
- **product-create-queue** - Topic for product creation events

### Retry Policy
- **Max Attempts**: 4 (1 initial + 3 retries)
- **Backoff Interval**: 1000ms (1 second)
- Configured in `KafkaConfig.java`

### Brokers
Three Kafka brokers running on:
- localhost:9092 (Broker 1)
- localhost:9093 (Broker 2)
- localhost:9094 (Broker 3)

## Database Access

Access the H2 console at: http://localhost:8080/h2-console

**Connection details:**
- JDBC URL: `jdbc:h2:mem:productdb`
- Username: `sa`
- Password: (empty)

## Monitoring

### Check Kafka Topics

```bash
docker exec -it kafka-broker-1 kafka-topics --bootstrap-server localhost:9092 --list
```

### View Topic Messages

```bash
docker exec -it kafka-broker-1 kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic product-create-queue \
  --from-beginning
```

### Check Schema Registry

```bash
curl http://localhost:8081/subjects
```

## Stopping the Application

### Stop Spring Boot Application
Press `Ctrl+C` in the terminal running the application.

### Stop Docker Services

```bash
docker-compose down
```

To remove volumes as well:

```bash
docker-compose down -v
```

## Testing the Retry Policy

To test the retry mechanism, you can modify the `ProductConsumer.processProductEvent()` method to throw an exception for specific conditions, and observe the retry behavior in the logs.

## Troubleshooting

### Issue: Kafka connection refused
**Solution**: Ensure Docker containers are running: `docker-compose ps`

### Issue: Schema Registry unavailable
**Solution**: Wait for Schema Registry to fully start (may take 30-60 seconds)

### Issue: Avro classes not generated
**Solution**: Run `mvn clean install` to generate classes from Avro schema

### Issue: Port already in use
**Solution**: Stop conflicting services or change ports in `docker-compose.yml` and `application.properties`

## Technologies Used

- Spring Boot 3.1.5
- Spring Kafka
- Apache Kafka 7.5.0
- Confluent Schema Registry 7.5.0
- Apache Avro 1.11.3
- H2 Database
- Lombok
- Maven

## License

This project is open source and available for educational purposes.