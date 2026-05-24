# Order Processing System - Microservices Sandbox

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![Kafka](https://img.shields.io/badge/Kafka-Event%20Streaming-black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Docker](https://img.shields.io/badge/Docker-Containerization-blue)

A comprehensive local microservices sandbox demonstrating the integration of modern enterprise technologies without relying on cloud-managed services.

## 🎯 Features & Technologies Demonstrated

*   **Java 21 & Virtual Threads**: The Order Service (Producer) is configured to utilize Java 21's Virtual Threads for highly scalable request handling.
*   **Java 8+ Streams API**: The Fulfillment Service demonstrates complex collection manipulation using the Streams API.
*   **Event-Driven Architecture**: Uses **Apache Kafka** as the event backbone to communicate between microservices asynchronously.
*   **Database & Data Access**: Uses **PostgreSQL** with two distinct data access patterns:
    *   **Spring Data JPA/Hibernate**: Used in the Order Service for standard CRUD operations.
    *   **JdbcTemplate**: Used in the Fulfillment Service to demonstrate complex or native querying.
*   **Dynamic Configuration**: A **Spring Cloud Config Server** manages properties dynamically. The Order Service utilizes `@RefreshScope` to update configurations at runtime without restarts.
*   **Observability**: Integrated with **Micrometer**, **Prometheus**, **Grafana**, and **Zipkin** for complete metrics scraping and distributed tracing across microservices and Kafka.
*   **Containerization & Orchestration**: Includes `Dockerfile`s for each service, a complete `docker-compose.yml` for local infrastructure, and basic **Kubernetes** manifests (`deployment.yaml`, `service.yaml`).

## 🏗️ Architecture Overview

```mermaid
graph TD
    Client(["Client API Request"]) --> OrderService("Order Service")
    OrderService -- "JPA (Save PENDING)" --> DB[("PostgreSQL")]
    OrderService -- "Produces Event" --> Kafka[["Kafka (orders-topic)"]]
    Kafka -- "Consumes Event" --> FulfillmentService("Fulfillment Service")
    FulfillmentService -- "JdbcTemplate (Update Status)" --> DB
    
    ConfigServer("Spring Cloud Config") -. "Serves Config" .-> OrderService
    ConfigServer -. "Serves Config" .-> FulfillmentService
```

## 🚀 How to Run Locally

### 1. Start Infrastructure
Start the supporting infrastructure (PostgreSQL, Kafka, Zookeeper, Zipkin, Prometheus, Grafana) using Docker Compose:
```bash
docker-compose up -d
```

### 2. Start the Microservices
You need to start the Spring Boot applications. **Important: Start the Config Server first.**

**Terminal 1 (Config Server):**
```bash
cd config-server
./mvnw spring-boot:run
```

**Terminal 2 (Order Service):**
```bash
cd order-service
./mvnw spring-boot:run
```

**Terminal 3 (Fulfillment Service):**
```bash
cd fulfillment-service
./mvnw spring-boot:run
```

## 🧪 Testing the System

### 1. Create an Order
Send a POST request to the Order Service (running on port `8081`):

```bash
curl -X POST http://localhost:8081/api/orders \
-H "Content-Type: application/json" \
-d '{
    "customerId": "CUST123",
    "items": ["Laptop", "Mouse", "Keyboard"],
    "totalAmount": 1250.00
}'
```

### 2. Verify Output
1. The Order Service saves the order as `PENDING` and produces an event to Kafka.
2. The Fulfillment Service logs will show it consuming the message and processing the items.
3. Check the PostgreSQL database (`orderdb`) to see the records in both the `orders` and `fulfillment_status` tables.

### 3. Dynamic Configuration Refresh
1. Modify `app.dynamic.message` in `config-repo/order-service.yml`.
2. View the current message: `curl http://localhost:8081/api/orders/config-message`
3. Trigger a refresh: `curl -X POST http://localhost:8081/actuator/refresh`
4. View the message again to see it updated without restarting the service!

### 4. Observability Dashboards
- **Zipkin Tracing**: Navigate to `http://localhost:9411`
- **Prometheus Metrics**: Navigate to `http://localhost:9090`
- **Grafana**: Navigate to `http://localhost:3000` (User: `admin`, Pass: `admin`)

## ☸️ Kubernetes Deployment

Basic Kubernetes manifests are provided in the `k8s/` directory.

1. Build the local Docker images:
```bash
cd config-server && docker build -t config-server:latest .
cd ../order-service && docker build -t order-service:latest .
cd ../fulfillment-service && docker build -t fulfillment-service:latest .
```
2. Apply the manifests:
```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```
