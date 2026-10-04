# Spring Kubernetes Delivery

A production-style delivery demo built with **Java 21, Spring Boot, Docker, Kubernetes, and GitHub Actions**.

The project demonstrates how a Spring Boot application can move through a modern delivery pipeline:

**Code → Test → Package → Docker Image → Container Registry → Kubernetes**

It includes health checks, multiple replicas, resource limits, configuration management, rolling deployments, and automated container publishing to GitHub Container Registry.

---

## Architecture

```text
Developer
    |
    | git push
    v
GitHub Repository
    |
    v
GitHub Actions
    |
    +--> Maven Test
    |
    +--> Maven Package
    |
    +--> Docker Build
    |
    +--> Push Image
            |
            v
GitHub Container Registry (GHCR)
            |
            v
       Kubernetes
            |
        Deployment
        /        \
     Pod #1     Pod #2
        \        /
          Service
             |
             v
      Spring Boot API
```

---

## Features

- Java 21
- Spring Boot 3
- Maven
- Spring Boot Actuator
- Automated tests with MockMvc
- Multi-stage Docker build
- Kubernetes Deployment
- Two application replicas
- Kubernetes Service
- ConfigMap-based configuration
- Kubernetes Secret example
- Liveness probes
- Readiness probes
- CPU and memory resource limits
- RollingUpdate deployment strategy
- Zero-unavailable-pod rolling deployment configuration
- GitHub Actions CI pipeline
- Automated Docker image publishing
- GitHub Container Registry (GHCR)

---

## API

### Application information

```http
GET /api/info
```

Example:

```json
{
  "status": "running",
  "version": "0.2.0",
  "application": "spring-k8s-delivery"
}
```

### Health Check

```http
GET /actuator/health
```

Example:

```json
{
  "status": "UP"
}
```

The health endpoint is also used by Kubernetes for liveness and readiness checks.

---

## Run Locally

### Requirements

- Java 21
- Maven

Run the application:

```bash
mvn spring-boot:run
```

Test:

```bash
curl http://localhost:8080/api/info
curl http://localhost:8080/actuator/health
```

---

## Run with Docker

Build:

```bash
docker build -t spring-k8s-delivery:0.2.0 .
```

Run:

```bash
docker run --rm \
  -p 18090:8080 \
  -e APP_VERSION=0.2.0 \
  spring-k8s-delivery:0.2.0
```

Test:

```bash
curl http://localhost:18090/api/info
```

The Dockerfile uses a multi-stage build so Maven is only required during the build stage.

---

## Kubernetes Deployment

The Kubernetes manifests are located in:

```text
k8s/
├── configmap.yml
├── deployment.yml
├── secret.yml
└── service.yml
```

Deploy:

```bash
kubectl apply -f k8s/
```

Check the deployment:

```bash
kubectl get deployments
kubectl get pods
kubectl get services
```

Wait for the rollout:

```bash
kubectl rollout status deployment/spring-k8s-delivery
```

For a local cluster, the service can be tested using port forwarding:

```bash
kubectl port-forward service/spring-k8s-delivery 18090:8080
```

Then:

```bash
curl http://localhost:18090/api/info
```

---

## Health Checks

Kubernetes uses Spring Boot Actuator to determine whether application instances are healthy.

The Deployment contains both:

- `readinessProbe`
- `livenessProbe`

using:

```text
/actuator/health
```

This prevents Kubernetes from routing traffic to application instances that are not ready.

---

## Rolling Updates

The Deployment uses Kubernetes `RollingUpdate` strategy:

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxUnavailable: 0
    maxSurge: 1
```

This configuration allows Kubernetes to create a replacement Pod before removing an existing available Pod.

Example image update:

```bash
kubectl set image deployment/spring-k8s-delivery \
  spring-k8s-delivery=spring-k8s-delivery:0.2.0
```

Monitor the deployment:

```bash
kubectl rollout status deployment/spring-k8s-delivery
```

During testing, the application was successfully rolled from version `0.1.0` to `0.2.0` while maintaining available replicas.

---

## CI/CD Pipeline

GitHub Actions automatically runs for pushes and pull requests targeting `main`.

Pipeline:

```text
Checkout
   |
   v
Setup Java 21
   |
   v
Maven Tests
   |
   v
Maven Package
   |
   v
Docker Build
   |
   v
Publish to GHCR
```

The workflow is located at:

```text
.github/workflows/ci.yml
```

Docker images are published to:

```text
ghcr.io/arman-sadeghian/spring-k8s-delivery
```

Images are tagged with both:

```text
latest
<git-commit-sha>
```

Using the commit SHA provides traceability between source code and the generated container image.

---

## Configuration

Runtime configuration is injected through Kubernetes ConfigMaps.

Example:

```yaml
data:
  APP_VERSION: "0.2.0"
  APP_ENVIRONMENT: "kubernetes"
```

The repository also contains a non-sensitive example Kubernetes Secret for demonstration purposes.

Production credentials should never be committed to source control.

---

## Resource Management

The application defines Kubernetes resource requests and limits:

```yaml
resources:
  requests:
    memory: "128Mi"
    cpu: "100m"
  limits:
    memory: "256Mi"
    cpu: "500m"
```

This allows Kubernetes to make better scheduling decisions and prevents an application container from consuming unlimited cluster resources.

---

## Project Structure

```text
spring-k8s-delivery/
├── .github/
│   └── workflows/
│       └── ci.yml
├── k8s/
│   ├── configmap.yml
│   ├── deployment.yml
│   ├── secret.yml
│   └── service.yml
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
│       └── java/
├── Dockerfile
├── pom.xml
└── README.md
```

---

## Delivery Flow

A typical change follows this path:

```text
Developer Change
      ↓
Git Push
      ↓
GitHub Actions
      ↓
Automated Tests
      ↓
Application Package
      ↓
Docker Image
      ↓
GitHub Container Registry
      ↓
Kubernetes Deployment
      ↓
Readiness Check
      ↓
Traffic
```

---

## Purpose

This repository is a portfolio project demonstrating practical backend delivery and DevOps concepts around a Spring Boot application, including containerization, Kubernetes deployment, rolling updates, health management, and automated CI/CD.

## Author

**Arman Sadeghian**