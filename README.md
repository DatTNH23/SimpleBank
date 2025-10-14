# SimpleBank - Cloud Native Microservices Demo

SimpleBank is a cloud‑native microservices demo showcasing Spring Boot microservices with service discovery, API gateway, centralized configuration, messaging, observability, and container/k8s deployments.

## Architecture

- **Config Server (`configserver`)**: Centralized externalized configuration via Spring Cloud Config (Git‑backed).
- **Service Discovery (`eurekaserver`)**: Service registry using Netflix Eureka.
- **API Gateway (`gatewayserver`)**: Edge gateway using Spring Cloud Gateway, OAuth2 resource server.
- **Domain Services**:
  - `accounts` (port 8080)
  - `loans` (port 8090)
  - `cards` (port 9000)
- **Messaging (`message`)**: Spring Cloud Stream + Kafka for async communication.
- **Datastores**: MySQL for each service (`accountsdb`, `loansdb`, `cardsdb`).
- **Security**: Keycloak for JWT issuance in local compose environment.
- **Caching**: Redis used by `gatewayserver`.
- **Observability**: Prometheus, Grafana, Loki, Tempo, and Promtail via compose/Helm.

### Service Ports (default/dev)
- Config Server: 8071
- Eureka Server: 8070
- API Gateway: 8072
- Accounts: 8080
- Loans: 8090
- Cards: 9000
- Kafka: 9092
- Redis: 6379
- Keycloak: 7080 (maps to container 8080)
- Grafana: 3000
- Loki Gateway: 3100 (write: 3102, read: 3101 inside network)
- Tempo: 3110
- Prometheus: 9090
- MySQL:
  - accountsdb: 3307 (host) -> 3306 (container)
  - loansdb: 3308 -> 3306
  - cardsdb: 3309 -> 3306

## Repositories
- App config repo used by Config Server: `https://github.com/DatTNH23/simplebank-config.git` (branch `main`).

## Prerequisites
- Docker and Docker Compose
- JDK 17+
- Maven 3.9+
- kubectl and Helm (for Kubernetes option)

## Profiles
- To stay up-to-date with the latest centralized configuration, run services with the Spring profile `default`.
- When running locally, prefer `SPRING_PROFILES_ACTIVE=default` unless you have a specific reason to pin to another profile.

## Quick Start (Docker Compose)
Choose an environment folder under `docker-compose/` and run compose. The `default` stack includes infra (Kafka, Redis, Keycloak, Observability) and microservices using images `tnh23/*:v10`.

Note: The containers are designed to use the `default` Spring profile so they always pick up the most recent config from the Config Server.

```bash
cd docker-compose/default
docker compose -f docker-compose.yml -f common-config.yml up -d
```

Wait until health checks report UP. Services will be available at:
- Config Server: http://localhost:8071/actuator/health
- Eureka: http://localhost:8070/
- Gateway: http://localhost:8072/actuator
- Accounts: http://localhost:8080/actuator
- Loans: http://localhost:8090/actuator
- Cards: http://localhost:9000/actuator
- Grafana: http://localhost:3000/
- Kafka broker: localhost:9092
- Redis: localhost:6379
- Keycloak: http://localhost:7080/
- Loki Gateway: http://localhost:3100/
- Prometheus: http://localhost:9090/

Stop stack:
```bash
docker compose down -v
```

### Compose Profiles
- `docker-compose/prod` and `docker-compose/qa` provide leaner stacks focused on services and MySQL databases (images `tnh23/*:v4`).

## Local Development (Run from source)
You can run each microservice locally while still using the compose infrastructure (MySQL, Kafka, Redis, Keycloak, etc.).

1) Start infra only (optional):
- Use the `default` compose but comment out app images or run a minimal infra file of your choice.

2) Update `application.yml` (already configured for local dev):
- Each service points to:
  - Config Server: `http://localhost:8071/` (`spring.config.import`)
  - Eureka: `http://localhost:8070/eureka/`
  - Local MySQL ports: accounts 3307, loans 3308, cards 3309
  - Kafka: `localhost:9092` (`accounts` also supports broker override via env)

2.1) Use the `default` profile when running from source:
```bash
SPRING_PROFILES_ACTIVE=default ./mvnw spring-boot:run
```

3) Build and run services:
```bash
# from repo root
cd configserver && ./mvnw spring-boot:run
# in new terminals
cd ../eurekaserver && ./mvnw spring-boot:run
cd ../gatewayserver && ./mvnw spring-boot:run
cd ../accounts && ./mvnw spring-boot:run
cd ../loans && ./mvnw spring-boot:run
cd ../cards && ./mvnw spring-boot:run
```

## Kubernetes Deployment
Kubernetes manifests are in `kubernetes/` and Helm charts under `helm/`.

### Plain Manifests
Apply sequentially (config -> infra -> services):
```bash
kubectl apply -f kubernetes/2_configmaps.yaml
kubectl apply -f kubernetes/3_configserver.yml
kubectl apply -f kubernetes/4_eurekaserver.yml
kubectl apply -f kubernetes/5_accounts.yml
kubectl apply -f kubernetes/6_loans.yml
kubectl apply -f kubernetes/7_cards.yml
kubectl apply -f kubernetes/8_gateway.yml
```

### Helm (Recommended)
- Base common templates and per‑service charts under `helm/simplebank-services/`.
- Environment umbrella charts under `helm/enviroments/{dev-env,qa-env,prod-env}`.

Install dev environment (example):
```bash
cd helm/enviroments/dev-env
helm dependency update
helm install simplebank-dev .
```

Grafana, Loki, Tempo, Kafka, Keycloak, and kube‑prometheus charts are provided under `helm/` for full observability and security setup. Customize via the `values.yaml` in each chart.

## Configuration
- Config Server pulls from Git: `simplebank-config` repo (branch `main`).
- Service configs overrideable via environment variables (see compose files) and `application.yml`.
- OAuth2/JWT validation in `gatewayserver` via Keycloak JWKS URL.

## Messaging
- Kafka topics used:
  - `send-communication`
  - `communication-sent`
- `accounts` defines `updateCommunication` function and uses Spring Cloud Stream bindings (see `accounts/src/main/resources/application.yml`).

## Observability
- Prometheus scrapes actuator metrics. Default actuator exposure is `*` in services.
- Grafana pre-provisioned with data sources (Prometheus, Loki, Tempo) via compose.
- Loki/Promtail aggregate logs; Tempo collects traces.

## Useful Commands
```bash
# Build all services
./mvnw -q -ntp -DskipTests package

# Start default compose (full stack)
cd docker-compose/default && docker compose up -d

# Tail service logs
docker compose logs -f accounts-ms

# Port-forward k8s service
kubectl -n default port-forward svc/accounts 8080:8080
```

## Troubleshooting
- If Config Server isn’t ready, services may fail to start. Check: `http://localhost:8071/actuator/health`.
- Ensure MySQL containers are healthy before starting services. Ports 3307/3308/3309 must be free.
- Keycloak must be running for the gateway JWT resource server to validate tokens (`http://localhost:7080/realms/master/...`).
- Kafka broker must be reachable at `localhost:9092` for messaging features.
- For compose health checks, wait until containers report `healthy`.

## License
This project is for educational/demo purposes.
