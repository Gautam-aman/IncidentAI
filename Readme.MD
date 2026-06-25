# IncidentAI

IncidentAI is a distributed incident management platform for creating, tracking, searching, discussing, and enriching operational incidents. The project demonstrates a production-style microservices architecture with Spring Boot services, event-driven communication, a FastAPI RAG service, a static operations console, and a Docker-based observability stack.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Frontend Console](#frontend-console)
- [API Reference](#api-reference)
- [Observability](#observability)
- [Local Development](#local-development)
- [Security Notes](#security-notes)
- [Roadmap](#roadmap)

## Overview

IncidentAI provides the core building blocks of an incident response platform:

- JWT-based authentication with refresh tokens and logout token blacklisting.
- Incident lifecycle management, including creation, assignment, status changes, comments, and audit history.
- Kafka events for incident creation, assignment, and status changes.
- Search indexing with Elasticsearch.
- Real-time incident chat over WebSocket/STOMP with Redis-backed messaging.
- Retrieval-augmented incident assistance using FastAPI, embeddings, and Qdrant.
- Metrics, tracing, and logs through Prometheus, Grafana, Jaeger, Elasticsearch, Kibana, and Logstash.

## Architecture

```text
                         +-------------------+
                         |  Frontend Console |
                         |  HTML/CSS/JS      |
                         +---------+---------+
                                   |
                                   v
                         +-------------------+
                         |  Gateway Service  |
                         |  Spring Gateway   |
                         +---------+---------+
                                   |
          +------------------------+------------------------+
          |                        |                        |
          v                        v                        v
+------------------+     +------------------+     +------------------+
|  Auth Service    |     |  Ticket Service  |     |  Search Service  |
|  Spring Boot     |     |  Spring Boot     |     |  Spring Boot     |
+--------+---------+     +--------+---------+     +--------+---------+
         |                        |                        |
         v                        v                        v
+------------------+     +------------------+     +------------------+
|  PostgreSQL      |     |  Kafka           |     |  Elasticsearch   |
+------------------+     +--------+---------+     +------------------+
                                  |
                   +--------------+--------------+
                   |                             |
                   v                             v
         +------------------+          +------------------+
         | Notification    |          | Chat Service     |
         | Service         |          | WebSocket/Redis  |
         +------------------+          +------------------+

                         +-------------------+
                         |  RAG Service      |
                         |  FastAPI/Qdrant   |
                         +-------------------+
```

All external API calls should go through the gateway at `http://localhost:8080` when the full stack is running.

## Services

| Service | Port | Responsibility |
| --- | ---: | --- |
| `gateway-service` | `8080` | Central API gateway, CORS, routing, request logging, correlation IDs |
| `auth-service` | `8081` | Registration, login, JWT issuing, refresh token rotation, logout, user profile |
| `ticket-service` | `8082` | Incident CRUD workflows, assignment, status updates, comments, audit trail, Kafka publishing |
| `notification-service` | `8084` | Kafka consumers for incident events and notification handling |
| `search-service` | `8085` | Elasticsearch-backed incident search and Kafka indexing |
| `chat-service` | `8086` | WebSocket/STOMP incident room messaging backed by Redis |
| `rag-service` | `8000` | Document upload, embeddings, vector search, and incident assistance |

## Tech Stack

- Backend: Java 21, Spring Boot, Spring Security, Spring Cloud Gateway, Spring Data JPA, Spring Kafka, Spring WebSocket
- AI/RAG: Python, FastAPI, LangChain, sentence-transformers, Qdrant
- Data: PostgreSQL, Redis, Elasticsearch
- Messaging: Apache Kafka and Zookeeper
- Frontend: HTML, CSS, vanilla JavaScript
- Observability: Spring Actuator, Micrometer, Prometheus, Grafana, Jaeger, Logstash, Kibana
- Runtime: Docker and Docker Compose

## Project Structure

```text
IncidentAI/
|-- auth-service/             # Authentication and token management
|-- ticket-service/           # Incident lifecycle service
|-- search-service/           # Elasticsearch indexing and search
|-- notification-service/     # Kafka-driven notification consumers
|-- chat-service/             # Incident chat over WebSocket/STOMP
|-- gateway-service/          # API gateway and service routing
|-- rag-service/              # FastAPI retrieval service
|-- frontend/                 # Static operations console
|-- docker/                   # Database initialization scripts
|-- observability/            # Prometheus, Grafana, and Logstash config
|-- docker-compose.yml        # Full local platform definition
`-- ControlFlow.png           # Service flow diagram used by the console
```

## Prerequisites

For the Docker-first workflow:

- Docker
- Docker Compose

For running services directly:

- Java 21
- Maven, or the included service-level `mvnw` wrappers
- Python 3.11+
- PostgreSQL
- Redis
- Kafka
- Elasticsearch
- Qdrant

## Quick Start

Start the complete platform:

```bash
docker compose up --build
```

Run it in the background:

```bash
docker compose up --build -d
```

Check service health through the gateway:

```bash
curl http://localhost:8080/monitor/auth/health
curl http://localhost:8080/monitor/ticket/health
curl http://localhost:8080/monitor/search/health
curl http://localhost:8080/monitor/notification/health
curl http://localhost:8080/monitor/chat/health
curl http://localhost:8080/monitor/rag/health
```

Stop the stack:

```bash
docker compose down
```

Remove persisted volumes when you want a clean reset:

```bash
docker compose down -v
```

## Frontend Console

The frontend is a static operations console located in `frontend/`.

Open `frontend/index.html` in a browser and keep the gateway URL set to:

```text
http://localhost:8080
```

The console supports signing in, registering users, creating incidents, updating incident status, searching incidents, uploading RAG documents, asking RAG questions, checking monitoring links, and connecting to incident chat.

## API Reference

The gateway exposes the main APIs under `http://localhost:8080`.

### Authentication

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Register a user |
| `POST` | `/api/auth/login` | Log in and receive access/refresh tokens |
| `POST` | `/api/auth/refresh` | Rotate refresh token and issue a new access token |
| `GET` | `/api/auth/me` | Return the current authenticated user |
| `POST` | `/api/auth/logout` | Revoke refresh token and blacklist access token |

Example login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

### Incidents

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/incidents` | Create an incident |
| `GET` | `/api/incidents/{id}` | Get incident details |
| `PUT` | `/api/incidents/{id}/assign` | Assign an incident |
| `PUT` | `/api/incidents/{id}/status` | Update incident status |
| `POST` | `/api/incidents/{id}/comments` | Add an incident comment |
| `GET` | `/api/incidents/{id}/audit` | Get incident audit history |

Example incident creation:

```bash
curl -X POST http://localhost:8080/api/incidents \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <access-token>" \
  -d '{
    "title": "Payment service latency",
    "description": "Elevated latency on checkout requests",
    "priority": "HIGH",
    "severity": "P2",
    "reporterId": "<user-id>"
  }'
```

### Search

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/search?query={text}` | Search indexed incidents |

```bash
curl "http://localhost:8080/api/search?query=payment"
```

### RAG

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/upload` | Upload and index a document |
| `POST` | `/api/ask` | Ask a question against indexed context |

```bash
curl -X POST http://localhost:8080/api/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"What should I check first during payment latency?","limit":5}'
```

### Chat

The chat service accepts native WebSocket/STOMP traffic through the gateway:

```text
ws://localhost:8080/ws/native
```

Messages are sent to:

```text
/app/chat.send
```

Incident room subscriptions use:

```text
/topic/incidents/{incidentId}
```

## Observability

When the Docker Compose stack is running, the following tools are available:

| Tool | URL | Notes |
| --- | --- | --- |
| Prometheus | `http://localhost:9090` | Metrics scraping |
| Grafana | `http://localhost:3000` | Dashboards, default login `admin` / `admin` |
| Jaeger | `http://localhost:16686` | Distributed traces |
| Elasticsearch | `http://localhost:9200` | Search and log storage |
| Kibana | `http://localhost:5601` | Elasticsearch UI |
| Qdrant | `http://localhost:6333` | Vector database |

Spring Boot actuator endpoints are exposed per service and routed through gateway health aliases under `/monitor/*/health`.

## Local Development

You can run individual services directly when the required infrastructure is already available.

Example for a Spring Boot service:

```bash
cd auth-service
./mvnw spring-boot:run
```

Run tests for a Spring Boot service:

```bash
cd ticket-service
./mvnw test
```

Example for the RAG service:

```bash
cd rag-service
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

Important environment variables used by the Compose stack:

| Variable | Purpose |
| --- | --- |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | PostgreSQL username |
| `SPRING_DATASOURCE_PASSWORD` | PostgreSQL password |
| `SPRING_DATA_REDIS_HOST` | Redis host |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka broker list |
| `SPRING_ELASTICSEARCH_URIS` | Elasticsearch endpoint |
| `QDRANT_HOST` | Qdrant host |
| `QDRANT_PORT` | Qdrant port |
| `MANAGEMENT_OTLP_TRACING_ENDPOINT` | OTLP trace export endpoint |

## Security Notes

- The checked-in JWT secret and default database credentials are for local development only.
- Replace secrets with environment-managed values before deploying outside a local machine.
- The gateway currently routes service APIs and health checks; keep service-level security enabled for protected resources.
- Redis token blacklisting is used during logout to invalidate active access tokens before their natural expiry.
- Refresh tokens are persisted, rotated, and revoked on logout.

## Roadmap

- Harden gateway-level JWT validation and authorization policies.
- Add richer notification delivery channels and retry/dead-letter handling.
- Expand search filters and incident indexing coverage.
- Add production dashboards for MTTA, MTTR, SLA breach risk, incident volume, and team workload.
- Add Kubernetes manifests or Helm charts for cluster deployment.
- Add CI workflows for build, test, image publishing, and security checks.
- Externalize secrets and environment-specific configuration.

## Author

Aman Gautam

IncidentAI is built as a distributed systems and AI engineering project covering microservices, event-driven architecture, security, observability, and retrieval-augmented incident workflows.
