# EventSphere Platform

EventSphere Platform is the **infrastructure and platform layer** of the EventSphere microservices-based event management and booking system.

This repository contains the core **Spring Cloud infrastructure services** responsible for centralized configuration, service discovery, and API request routing.

The platform is designed to work together with the EventSphere backend microservices such as **User Service, Event Service, and Booking Service**.

---

## 🏗️ Architecture

The EventSphere platform provides the infrastructure required for communication between the individual microservices.

```text
                         ┌───────────────────────┐
                         │       Frontend        │
                         │     HTML / CSS / JS   │
                         └───────────┬───────────┘
                                     │
                                     ▼
                         ┌───────────────────────┐
                         │      API Gateway      │
                         │         :8080         │
                         └───────────┬───────────┘
                                     │
                    ┌────────────────┼────────────────┐
                    │                │                │
                    ▼                ▼                ▼
             ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
             │    User     │ │    Event    │ │   Booking   │
             │   Service   │ │   Service   │ │   Service   │
             └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
                    │               │               │
                    └───────────────┼───────────────┘
                                    │
                                    ▼
                         ┌───────────────────────┐
                         │    Eureka Server      │
                         │   Service Discovery   │
                         │         :8761         │
                         └───────────────────────┘

                         ┌───────────────────────┐
                         │    Config Server      │
                         │ Centralized Config     │
                         │         :8888         │
                         └───────────────────────┘
```

---

## 📦 Platform Components

This repository currently contains the following platform services:

```text
eventsphere-platform/
│
├── api-gateway/
│
├── config-server/
│
├── discovery-server/
│
├── pom.xml
│
└── README.md
```

The GitHub repository currently contains these three platform modules along with the Maven parent `pom.xml`.

---

# 🚪 API Gateway

The **API Gateway** acts as the single entry point for client requests.

Instead of communicating directly with individual microservices, clients send requests through the Gateway.

```text
Client
   │
   ▼
API Gateway
   │
   ├──────► User Service
   │
   ├──────► Event Service
   │
   └──────► Booking Service
```

### Responsibilities

* Centralized API entry point
* Request routing
* Service discovery integration
* JWT authentication/validation
* Forwarding requests to backend services
* Centralized cross-cutting concerns

### Default Port

```text
8080
```

Example:

```text
http://localhost:8080
```

---

# 🔍 Discovery Server

The **Discovery Server** is implemented using **Netflix Eureka**.

It allows microservices to register themselves and discover other services dynamically.

```text
                    Eureka Server
                         │
             ┌───────────┼───────────┐
             │           │           │
             ▼           ▼           ▼
        User Service Event Service Booking Service
```

### Responsibilities

* Service registration
* Service discovery
* Dynamic service lookup
* Microservice health/status visibility
* Avoiding hard-coded service IP addresses

### Default Port

```text
8761
```

Eureka Dashboard:

```text
http://localhost:8761
```

---

# ⚙️ Config Server

The **Config Server** provides centralized configuration management for all EventSphere microservices.

Instead of maintaining configuration separately inside every service, common and environment-specific configuration can be managed centrally.

```text
                    Config Server
                         │
          ┌──────────────┼──────────────┐
          │              │              │
          ▼              ▼              ▼
     User Service   Event Service   Booking Service
```

### Responsibilities

* Centralized configuration
* Environment-specific configuration
* Database configuration
* Eureka configuration
* Service configuration
* Shared application properties

### Default Port

```text
8888
```

---

# 🔄 Service Communication

The platform enables communication between microservices through:

### Eureka Service Discovery

Services register with Eureka:

```text
User Service ───────► Eureka
Event Service ──────► Eureka
Booking Service ────► Eureka
```

When one service needs another service, it can discover the destination dynamically.

---

### API Gateway

External requests are routed through the Gateway:

```text
Frontend
   │
   ▼
API Gateway
   │
   ├── /api/auth/**     → User Service
   │
   ├── /api/users/**    → User Service
   │
   ├── /api/events/**   → Event Service
   │
   └── /api/bookings/** → Booking Service
```

> The exact route definitions depend on the current Gateway configuration.

---

# 🔐 Security

The API Gateway can be used as the centralized security layer for the EventSphere platform.

The general authentication flow is:

```text
             Login Request
                  │
                  ▼
             API Gateway
                  │
                  ▼
             User Service
                  │
                  ▼
             JWT Token
                  │
                  ▼
             Client
```

For protected requests:

```text
Client
   │
   │ JWT
   ▼
API Gateway
   │
   │ Validate Token
   ▼
Backend Service
```

This architecture allows authentication concerns to be handled centrally rather than duplicating the same gateway-level logic across every service.

---

# 🛠️ Technology Stack

| Technology           | Purpose                       |
| -------------------- | ----------------------------- |
| Java 17              | Programming Language          |
| Spring Boot          | Application Framework         |
| Spring Cloud         | Microservices Infrastructure  |
| Spring Cloud Gateway | API Gateway                   |
| Netflix Eureka       | Service Discovery             |
| Spring Cloud Config  | Centralized Configuration     |
| Maven                | Build & Dependency Management |
| Docker               | Containerization              |
| Docker Compose       | Multi-container Deployment    |

---

# 📁 Project Structure

```text
eventsphere-platform/
│
├── api-gateway/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── config-server/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── discovery-server/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── pom.xml
│
└── README.md
```

---

# ⚙️ Prerequisites

Make sure the following are installed:

* Java 17+
* Maven
* Docker
* Docker Compose
* Git

Check Java:

```bash
java -version
```

Check Maven:

```bash
mvn -version
```

Check Docker:

```bash
docker --version
```

---

# 📥 Clone the Repository

```bash
git clone https://github.com/SahanRansika/eventsphere-platform.git
```

Navigate to the project:

```bash
cd eventsphere-platform
```

---

# 🔨 Build the Project

Build all modules using Maven:

```bash
mvn clean package
```

To skip tests:

```bash
mvn clean package -DskipTests
```

---

# 🐳 Docker

The platform services are designed to be containerized and integrated with the complete EventSphere environment.

### Build Docker Images

```bash
docker compose build
```

### Start Services

```bash
docker compose up -d
```

### Rebuild and Start

```bash
docker compose up -d --build
```

### Check Containers

```bash
docker compose ps
```

### View Logs

```bash
docker compose logs -f
```

View a specific service:

```bash
docker compose logs -f api-gateway
```

```bash
docker compose logs -f config-server
```

```bash
docker compose logs -f discovery-server
```

### Stop Services

```bash
docker compose down
```

---

# 🔌 Default Ports

| Component                 |   Port |
| ------------------------- | -----: |
| API Gateway               | `8080` |
| Discovery / Eureka Server | `8761` |
| Config Server             | `8888` |

Backend microservice ports are configured separately in the EventSphere microservices repository.

---

# 🔗 Related EventSphere Repository

The platform services work together with the EventSphere backend microservices.

### Backend Microservices

The backend repository contains:

* User Service
* Event Service
* Booking Service

Repository:

```text
https://github.com/SahanRansika/eventsphere-services
```

### Platform Repository

This repository contains:

* API Gateway
* Config Server
* Eureka / Discovery Server

```text
https://github.com/SahanRansika/eventsphere-platform
```

Together they form the complete EventSphere microservices architecture.

---

# 🔄 Complete EventSphere Architecture

```text
                           EVENTSPHERE
                               │
              ┌────────────────┴────────────────┐
              │                                 │
              ▼                                 ▼
       Platform Layer                    Business Layer
              │                                 │
    ┌─────────┼─────────┐              ┌────────┼────────┐
    │         │         │              │        │        │
    ▼         ▼         ▼              ▼        ▼        ▼
 API       Config    Eureka          User     Event   Booking
Gateway    Server    Server         Service  Service  Service
    │                                  │        │        │
    └──────────────────────────────────┼────────┼────────┘
                                       │        │
                                       ▼        ▼
                                  PostgreSQL  MongoDB
```

---

# 🚀 Startup Order

For a reliable startup, the platform infrastructure should become available before the business microservices.

Recommended order:

```text
1. Config Server
        │
        ▼
2. Eureka / Discovery Server
        │
        ▼
3. API Gateway
        │
        ▼
4. User Service
        │
        ▼
5. Event Service
        │
        ▼
6. Booking Service
```

When using Docker Compose with proper health checks and service dependencies, this startup process can be managed automatically.

---

# 🧪 Troubleshooting

### Check Running Containers

```bash
docker compose ps
```

### Check Gateway Logs

```bash
docker compose logs --tail=100 api-gateway
```

### Check Config Server Logs

```bash
docker compose logs --tail=100 config-server
```

### Check Eureka Logs

```bash
docker compose logs --tail=100 discovery-server
```

### Restart the Platform

```bash
docker compose down
docker compose up -d --build
```

### Check Eureka

Open:

```text
http://localhost:8761
```

Verify that the expected microservices are registered.

---

# 🔒 Configuration & Secrets

Production deployments should use environment variables or external configuration for sensitive values.

Do **not** commit the following values directly to a public GitHub repository:

* Database passwords
* JWT secrets
* API keys
* Private credentials
* Production URLs
* Cloud credentials

Recommended approach:

```text
Environment Variables
        │
        ▼
Config Server
        │
        ▼
Microservices
```

---

# 🎯 Key Features

* Centralized configuration
* Eureka service discovery
* API Gateway
* Microservice request routing
* JWT-based security integration
* Dynamic service discovery
* Centralized platform infrastructure
* Docker-ready architecture
* Spring Cloud ecosystem
* Independent microservice deployment

---

# 📚 Learning Objectives

This project demonstrates practical implementation of:

* Microservices architecture
* Spring Boot
* Spring Cloud
* API Gateway pattern
* Service Discovery pattern
* Centralized configuration
* Distributed service communication
* JWT authentication architecture
* Docker containerization
* Maven multi-module projects

---

# 👨‍💻 Author

**Sahan Ransika**

GitHub:

https://github.com/SahanRansika

---

# 📄 License

This project is developed for educational and academic purposes.
