# Spring Boot Apache Kafka Demo

A clean, production-ready demonstration of event-driven messaging using **Spring Boot 3.3.5**, **Apache Kafka (KRaft mode)**, and **Docker Compose**.

This project showcases how to configure and run an end-to-end Kafka setup with custom partitioned topics, key-based message routing, JSON serialization/deserialization, and consumer group load balancing.

---

## 🚀 Features & Concepts Demonstrated

- **KRaft Mode Kafka (ZooKeeper-less)**: Uses the official `apache/kafka:latest` Docker image running Kafka Raft metadata mode for lightweight local development.
- **Dynamic Topic Creation**: Automatically creates an `orders` topic with **3 partitions** on startup via Spring's `TopicBuilder`.
- **Key-Based Message Partitioning**: Uses `orderId` as the record key to guarantee order preservation per customer/order and demonstrate partition distribution.
- **Asynchronous Producer Callbacks**: Logs partition and offset metadata upon delivery confirmation using Java `CompletableFuture` callbacks.
- **Consumer Group Load Balancing**: Employs multiple consumers (`EmailConsumer` and `TempSameGroupConsumer`) within the same consumer group (`email-group`) to demonstrate how Kafka distributes partitions among competing consumers.
- **Safe JSON Serialization**: Configures `JsonSerializer` and `JsonDeserializer` with trusted package constraints and avoids class name leakage in message headers.

---

## 🛠️ Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.3.5
- **Messaging:** Apache Kafka (Spring Kafka)
- **Containerization:** Docker & Docker Compose
- **Build Tool:** Maven (Wrapper included)

---

## 📁 Project Structure

```text
kafka_demo/
├── docker-compose.yml              # Single-node Kafka broker (KRaft mode)
├── pom.xml                         # Project dependencies and plugins
├── src/main/
│   ├── java/com/kafkademo/
│   │   ├── KafkaDemoApplication.java
│   │   ├── config/
│   │   │   └── KafkaTopicConfig.java      # Topic bean definition (3 partitions)
│   │   ├── controller/
│   │   │   └── OrderController.java       # REST endpoint producing Kafka events
│   │   ├── consumer/
│   │   │   ├── EmailConsumer.java         # Consumer in 'email-group'
│   │   │   └── TempSameGroupConsumer.java # Co-consumer in 'email-group' for load balancing
│   │   └── dto/
│   │       ├── OrderPlacedEvent.java      # Kafka message payload record
│   │       └── OrderRequest.java          # REST incoming request record
│   └── resources/
│       └── application.properties         # Kafka producer & consumer configuration
└── README.md
```

---

## ⚡ Quick Start

### 1. Prerequisites
Ensure you have the following installed:
- [Java 21 JDK](https://adoptium.net/)
- [Docker & Docker Desktop](https://www.docker.com/)

---

### 2. Start Kafka Broker
Launch the single-node Kafka container running in KRaft mode:

```bash
docker compose up -d
```

Verify that the container is up and running on port `9092`:
```bash
docker ps
```

---

### 3. Run the Spring Boot Application
The application is configured to run on port **`18080`**.

- **On Linux / macOS:**
  ```bash
  ./mvnw spring-boot:run
  ```
- **On Windows (PowerShell / Command Prompt):**
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```

---

### 4. Produce Messages (Test the Flow)

Send a POST request to produce an `OrderPlacedEvent`:

#### Using cURL:
```bash
curl -X POST http://localhost:18080/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-101", "item":"Mechanical Keyboard", "quantity":1}'
```

#### Using PowerShell:
```powershell
Invoke-RestMethod -Uri "http://localhost:18080/orders" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"orderId":"ORD-101", "item":"Mechanical Keyboard", "quantity":1}'
```

Send multiple requests with varying `orderId` values (e.g. `ORD-101`, `ORD-102`, `ORD-103`) to see partition assignment in action.

---

### 5. Observe the Output

In your Spring Boot application logs, you will observe:

1. **Producer Confirmation:**
   ```text
   Published eventId=5f8e..., orderId=ORD-101, partition=1, offset=0
   ```
2. **Consumer Processing:**
   One of the consumers assigned to that partition will receive and process the message:
   ```text
   [EMAIL] eventId : 5f8e... orderId : ORD-101 item : Mechanical Keyboard quantity : 1
   ```
   or:
   ```text
   [TEMP_SAME_GRP_CONSUMER] eventId : 5f8e... orderId : ORD-101 item : Mechanical Keyboard quantity : 1
   ```

---

## ⚙️ Key Configuration Details

[`src/main/resources/application.properties`](src/main/resources/application.properties):

| Property | Value | Description |
| :--- | :--- | :--- |
| `server.port` | `18080` | Spring Boot HTTP server port |
| `spring.kafka.bootstrap-servers` | `localhost:9092` | Kafka broker connection endpoint |
| `spring.kafka.consumer.group-id` | `email-group` | Default consumer group ID |
| `spring.kafka.consumer.auto-offset-reset` | `earliest` | Reads from the beginning if no offset is committed |
| `spring.kafka.producer.key-serializer` | `StringSerializer` | Serializes record key (`orderId`) |
| `spring.kafka.producer.value-serializer` | `JsonSerializer` | Serializes `OrderPlacedEvent` to JSON |
| `spring.kafka.consumer.value-deserializer`| `JsonDeserializer` | Deserializes incoming JSON messages |
| `spring.json.trusted.packages` | `com.kafkademo.dto` | Prevents deserialization gadget attacks |
| `spring.json.use.type.headers` | `false` | Decouples payload type from producer package headers |

---

## 🛑 Stopping the Application

To shut down the Kafka Docker container:
```bash
docker compose down
```

To remove all volumes and cached containers:
```bash
docker compose down -v
```
