# IncidentAI 🚨

A distributed AI-powered Incident Management Platform built using Microservices, Event-Driven Architecture, RAG (Retrieval-Augmented Generation), Docker, Kubernetes, Kafka, Redis, PostgreSQL, and React.

---

# Overview

IncidentAI is a production-style platform that helps engineering teams manage incidents, outages, and operational issues.

The system provides:

- Authentication & Authorization
- Incident/Ticket Management
- AI-powered Incident Assistance (RAG)
- Real-Time Collaboration
- Notification System
- Search Service
- Analytics Dashboard
- Event-Driven Communication
- Distributed Deployment

---

# High Level Architecture

```text
                           ┌──────────────┐
                           │   Frontend   │
                           │ React + TS   │
                           └──────┬───────┘
                                  │
                                  ▼
                        ┌─────────────────┐
                        │   API Gateway   │
                        │ Spring Gateway  │
                        └───────┬─────────┘
                                │
        ┌───────────────────────┼─────────────────────────┐
        │                       │                         │
        ▼                       ▼                         ▼

┌──────────────┐      ┌────────────────┐      ┌────────────────┐
│ Auth Service │      │ Ticket Service │      │ Search Service │
└──────────────┘      └────────────────┘      └────────────────┘

        │                       │
        │                       │
        ▼                       ▼

┌──────────────┐      ┌────────────────┐
│ PostgreSQL   │      │ Kafka Events   │
└──────────────┘      └────────────────┘

                                │
                                ▼

        ┌─────────────────────────────────────┐
        │                                     │
        ▼                                     ▼

┌────────────────┐                 ┌────────────────┐
│ Notification   │                 │ Analytics      │
│ Service        │                 │ Service        │
└────────────────┘                 └────────────────┘

                                │
                                ▼

                      ┌─────────────────┐
                      │   RAG Service   │
                      │ FastAPI + LLM   │
                      └─────────────────┘
```

---

# Tech Stack

## Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Cloud Gateway
- Spring Data JPA
- PostgreSQL
- Redis
- Apache Kafka

## AI

- Python
- FastAPI
- LangChain
- Vector Database

## Frontend

- React
- TypeScript
- Tailwind CSS

## DevOps

- Docker
- Docker Compose
- Kubernetes
- GitHub Actions

## Observability

- Prometheus
- Grafana
- ELK Stack
- Jaeger

---

# Current Progress

## Completed

### Auth Service

Features:

- User Registration
- User Login
- BCrypt Password Encryption
- JWT Authentication
- Refresh Tokens
- Refresh Token Rotation
- Logout
- User Profile API
- Redis Token Blacklisting
- Role-Based Authorization

### API Gateway

Features:

- Centralized Routing
- Request Logging
- Correlation IDs
- Gateway Pattern
- Future JWT Validation

---

# Project Structure

```text
IncidentAI/
│
├── auth-service/
│
├── gateway-service/
│
├── ticket-service/
│
├── search-service/
│
├── notification-service/
│
├── analytics-service/
│
├── rag-service/
│
├── frontend/
│
├── docker/
│
├── k8s/
│
└── docs/
```

---

# Auth Service Architecture

```text
Controller
     │
     ▼
Service Layer
     │
     ▼
Repository Layer
     │
     ▼
PostgreSQL
```

Authentication Flow:

```text
Login
  │
  ▼
AuthenticationManager
  │
  ▼
CustomUserDetailsService
  │
  ▼
PostgreSQL
  │
  ▼
JWT Generation
  │
  ▼
Client
```

---

# API Gateway Architecture

```text
Frontend
   │
   ▼
Gateway
   │
   ├── Logging Filter
   ├── Correlation Filter
   ├── JWT Validation (Upcoming)
   │
   ▼
Microservices
```

---

# Security Design

## Access Token

- JWT
- 15 minutes expiry

## Refresh Token

- Database stored
- 7 days expiry
- Rotation enabled

## Password Storage

- BCrypt Hashing

## Logout

- Refresh token revoked
- JWT blacklisted in Redis

---

# Database Schema

## users

| Column | Type |
|----------|----------|
| id | UUID |
| name | VARCHAR |
| email | VARCHAR |
| password | VARCHAR |
| enabled | BOOLEAN |

---

## roles

| Column | Type |
|----------|----------|
| id | BIGINT |
| name | VARCHAR |

---

## refresh_tokens

| Column | Type |
|----------|----------|
| id | UUID |
| token | VARCHAR |
| user_id | UUID |
| expiry_date | TIMESTAMP |
| revoked | BOOLEAN |

---

# Upcoming Phases

## Phase 2

Ticket Service

Features:

- Create Incident
- Assign Incident
- Update Status
- Incident Timeline
- Audit Trail
- Kafka Event Publishing

---

## Phase 3

Notification Service

Features:

- Email Notifications
- Kafka Consumers
- Retry Handling
- Dead Letter Queue

---

## Phase 4

Search Service

Features:

- Elasticsearch Integration
- Full Text Search
- Incident Search

---

## Phase 5

Real-Time Chat

Features:

- WebSockets
- Redis Pub/Sub
- Incident War Rooms

---

## Phase 6

AI/RAG Service

Features:

- Document Upload
- Embeddings
- Vector Search
- AI Assistant
- Similar Incident Retrieval

---

## Phase 7

Analytics Service

Features:

- MTTR Metrics
- SLA Tracking
- Incident Trends
- Team Performance

---

# Running Locally

## Start Infrastructure

```bash
docker compose up -d
```

## Start Auth Service

```bash
cd auth-service

mvn spring-boot:run
```

## Start Gateway Service

```bash
cd gateway-service

mvn spring-boot:run
```

---

# Future Improvements

- Kubernetes Deployment
- Service Discovery
- Distributed Tracing
- Circuit Breakers
- Saga Pattern
- CQRS
- Event Sourcing
- Multi-Tenant Support

---

# Author

Aman Gautam

IncidentAI is being built as a production-grade distributed systems project to demonstrate:

- Backend Engineering
- Distributed Systems
- Microservices
- Cloud Native Development
- AI Engineering
- DevOps Practices