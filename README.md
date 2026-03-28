# 🚀 Distributed Rate Limiter (Kafka + Redis + Spring Boot)

A production-grade **distributed rate limiting system** built using **Spring Boot, Kafka, Redis, and WebFlux**.  
This project demonstrates event-driven architecture, idempotent processing, retries, DLQ handling, and service resilience.

---

## 🧱 Architecture

```
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
```

---

## ⚙️ Tech Stack

- Java 17
- Spring Boot 3 (WebFlux + MVC)
- Apache Kafka
- Redis
- Docker + Docker Compose
- k6 (Load Testing)

---

## 📦 Services

### 1. API Gateway
- Built with **Spring WebFlux**
- Uses a **WebFilter** for request interception
- Calls rate limiter service via **WebClient**
- Publishes events to Kafka
- Includes **Circuit Breaker (Resilience4j)**

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

### Circuit Breaker
- Prevents cascading failures
- Fallback mechanism when rate limiter is down

---

### Reactive Gateway
- Non-blocking request handling
- Backpressure-friendly

---

## 🐳 Running Locally (Recommended)

### 1. Start Infrastructure

```bash
docker compose up -d
```

This starts:
- Kafka
- Zookeeper
- Redis

---

### 2. Verify Containers

```bash
docker ps
```

---

### 3. Create Kafka Topics

```bash
docker exec -it kafka kafka-topics \
--bootstrap-server localhost:9092 \
--create \
--topic rate-limit-events \
--partitions 1 \
--replication-factor 1
```

```bash
docker exec -it kafka kafka-topics \
--bootstrap-server localhost:9092 \
--create \
--topic rate-limit-events-dlq \
--partitions 1 \
--replication-factor 1
```

```bash
docker exec -it kafka kafka-topics \
--bootstrap-server localhost:9092 \
--create \
--topic rate-limit-events-retry-1 \
--partitions 1 \
--replication-factor 1
```

```bash
docker exec -it kafka kafka-topics \
--bootstrap-server localhost:9092 \
--create \
--topic rate-limit-events-retry-2 \
--partitions 1 \
--replication-factor 1
```

---

### 4. Run Services

Run each service separately:

```bash
cd api-gateway && ./mvnw spring-boot:run
cd rate-limiter-service && ./mvnw spring-boot:run
cd analytics-service && ./mvnw spring-boot:run
```

---

## 🧪 Load Testing

```bash
k6 run loadtest.js
```

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
- Observability (Prometheus + Grafana)
- Kafka Streams for analytics
- Outbox pattern
- 
---

## 👨‍💻 Author

Sumukh

---

## ⭐ If you found this useful

