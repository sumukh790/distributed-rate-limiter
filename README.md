# Distributed Rate Limiter (Kafka + Redis + Spring Boot)

A production-grade **distributed rate limiting system** built using **Spring Boot, Kafka, Redis, and WebFlux**.  
This project demonstrates event-driven architecture, idempotent processing, retries, and fault tolerance.

---

## Architecture distributed-rate-limiter
Client Request
↓
API Gateway (WebFlux Filter)
↓
Rate Limiter Service (Redis + Lua)
↓
Kafka Producer
↓
Kafka Topic (rate-limit-events)
↓
Analytics Service (Consumer)
↓
Retry → DLQ (rate-limit-events-dlq)

---

## ⚙️ Tech Stack

- Java 17
- Spring Boot 3 (WebFlux + MVC)
- Apache Kafka
- Redis
- Docker
- k6 (Load Testing)

---

## 📦 Services

### 1. API Gateway
- Built with **Spring WebFlux**
- Uses a **WebFilter** for request interception
- Calls rate limiter service via **WebClient**
- Publishes events to Kafka

---

### 2. Rate Limiter Service
- Uses **Redis + Lua script**
- Implements **Token Bucket algorithm**
- Ensures atomic rate limiting

---

### 3. Analytics Service
- Kafka consumer
- Handles:
    - Manual acknowledgment
    - Retry mechanism
    - Dead Letter Queue (DLQ)
    - Idempotent processing using Redis

---

## 🔥 Key Features

### Idempotent Producer
- `acks=all`
- `enable.idempotence=true`
- Safe retries without duplication

---

### Idempotent Consumer
- Uses Redis (`SETNX`)
- Prevents duplicate processing
- TTL-based cleanup

---

### Retry + DLQ
- Retries failed messages
- Sends to `rate-limit-events-dlq` after max retries
- Prevents infinite retry loops

---

### At-Least-Once Delivery
- Manual offset commit
- No message loss

---

### Reactive Gateway
- Non-blocking request handling
- Backpressure-friendly

---

## 🐳 Running Locally

### Start Zookeeper
docker run -d
–name zookeeper
-p 2181:2181
confluentinc/cp-zookeeper:7.5.0

---

### Start Kafka
docker run -d
–name kafka
-p 9092:9092
–link zookeeper
-e KAFKA_BROKER_ID=1
-e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1
confluentinc/cp-kafka:7.5.0

---

### Create Topics
docker exec -it kafka kafka-topics
–bootstrap-server localhost:9092
–create
–topic rate-limit-events
–partitions 1
–replication-factor 1

docker exec -it kafka kafka-topics
–bootstrap-server localhost:9092
–create
–topic rate-limit-events-dlq
–partitions 1
–replication-factor 1

---

### Start Redis
docker run -d -p 6379:6379 redis

---

### Run Services
./mvnw spring-boot:run

(Start each service separately)

---

## 🧪 Load Testing
k6 run loadtest.js

---

## ⚠️ Lessons Learned

- Kafka idempotence ≠ end-to-end idempotency
- Consumer-side deduplication is critical
- DLQ must be configured on the consumer side
- Serialization mismatches can break pipelines
- Manual offset control is essential for reliability

---

## 🚀 Future Improvements

- Retry topics (non-blocking retries)
- Circuit breaker (Resilience4j)
- Observability (Prometheus + Grafana)
- Kafka Streams for analytics
- Outbox pattern

---

## 👨‍💻 Author

Sumukh

---

## ⭐ If you found this useful
