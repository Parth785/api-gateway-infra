PROJECT CONTEXT – EVENT-DRIVEN MICROSERVICES SYSTEM

Developer: Parth Lakhani

Goal:
Build a production-style microservices backend system using Spring Boot, Kafka, Docker, and event-driven architecture to learn distributed systems design.

SYSTEM ARCHITECTURE

Client
↓
API Gateway (Spring Cloud Gateway + JWT Authentication)
↓
Microservices
• user-service
• product-service
• order-service
• notification-service
↓
Infrastructure
• Kafka (event streaming)
• PostgreSQL (per service database)
• Docker containers
↓
External System
• Gmail SMTP (email delivery)

TECHNOLOGY STACK

Backend
• Java 17
• Spring Boot
• Spring Cloud Gateway
• Spring Kafka
• Spring Data JPA
• Spring Security (JWT)

Infrastructure
• Apache Kafka
• Zookeeper
• PostgreSQL
• Docker

External
• Gmail SMTP for sending emails

SERVICES DESCRIPTION

1. API GATEWAY
   Port: 8080

Responsibilities:
• Entry point for all client requests
• JWT authentication validation
• Inject user identity into downstream requests

Key Implementation:
• GlobalFilter validates Authorization header
• Extracts userId from JWT
• Adds header:
X-User-Id

Example flow:
Client → Gateway
Authorization: Bearer JWT
↓
Gateway validates JWT
↓
Gateway forwards request to microservice

Services routed through gateway:
• /users/**
• /products/**
• /orders/**

2. USER SERVICE
   Port: 8085

Responsibilities:
• User registration
• Login authentication
• JWT token generation
• Store user data in PostgreSQL

Key features:
• BCrypt password hashing
• JWT generation with userId claim
• REST endpoints for retrieving user info

Example endpoints:

POST /users
Register new user

POST /users/login
Returns JWT token

GET /users/{id}
Returns user information including email

Database:
PostgreSQL container

3. PRODUCT SERVICE
   Port: 8087

Responsibilities:
• Product catalog
• Product CRUD operations

Example endpoints:

GET /products
GET /products/{id}
POST /products
PUT /products/{id}

Database:
PostgreSQL

4. ORDER SERVICE
   Port: 8082

Responsibilities:
• Create orders
• Calculate order totals
• Publish events to Kafka

Order creation flow:

Client
↓
Gateway (JWT validation)
↓
Order Service

Steps executed:

1. Extract userId from header X-User-Id
2. Fetch user email from user-service using RestTemplate
3. Fetch product info from product-service
4. Calculate order total
5. Save order in PostgreSQL
6. Publish Kafka event

Kafka Event Produced

Topic:
order-created

Event Structure:

{
"orderId": 24,
"userId": 3,
"userEmail": "[user@example.com](mailto:user@example.com)",
"totalPrice": 1200.0
}

Producer implementation:

kafkaTemplate.send("order-created", event);

5. NOTIFICATION SERVICE
   Port: 8090

Responsibilities:
• Listen to Kafka events
• Send email notifications

Kafka consumer listens to:

Topic:
order-created

Consumer flow:

Kafka Event
↓
Notification Service
↓
Deserialize event
↓
Send email via SMTP

Example consumer:

@KafkaListener(topics = "order-created", groupId = "notification-group")

Email content:

Subject:
Order Confirmation

Body:
Your order has been placed successfully.

INFRASTRUCTURE

Docker Containers

PostgreSQL:
docker run --name user-postgres 
-e POSTGRES_DB=userdb 
-e POSTGRES_USER=user 
-e POSTGRES_PASSWORD=password 
-p 5433:5432 
-d postgres:15

Kafka Setup

Zookeeper:
docker run -d --name zookeeper -p 2181:2181 
-e ZOOKEEPER_CLIENT_PORT=2181 
confluentinc/cp-zookeeper:7.4.0

Kafka Broker:
docker run -d --name kafka -p 9092:9092 
-e KAFKA_ZOOKEEPER_CONNECT=host.docker.internal:2181 
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 
confluentinc/cp-kafka:7.4.0

EMAIL CONFIGURATION

SMTP Provider:
Gmail

Configuration:

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=[your-email@gmail.com](mailto:your-email@gmail.com)
spring.mail.password=app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

EVENT FLOW SUMMARY

Create Order
↓
Order Service saves order
↓
Order Service publishes Kafka event
↓
Kafka topic order-created
↓
Notification Service receives event
↓
Email sent to user

CURRENT PROJECT STATUS

Working Components:
✔ API Gateway with JWT
✔ User registration + login
✔ Product service
✔ Order service
✔ PostgreSQL integration
✔ Kafka producer
✔ Kafka consumer
✔ Notification service
✔ Email sending

PLANNED IMPROVEMENTS

Kafka Reliability
• Retry topics
• Dead letter queues

Infrastructure
• Dockerize all services
• Docker Compose orchestration

Observability
• Spring Boot Actuator
• Prometheus metrics
• Grafana dashboards

Deployment
• Kubernetes
• AWS EKS
• CI/CD pipelines

PROJECT GOAL

Demonstrate production-style microservices architecture including:

• authentication
• event-driven communication
• service isolation
• asynchronous processing
• distributed system design
