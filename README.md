# Java 21 Spring Boot 3 DevOps

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-green)
![Docker](https://img.shields.io/badge/Docker-Optimized-blue)

## Table of Contents
Quick links to navigate this README.

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
  - [Local Development](#local-development)
  - [Docker Deployment](#docker-deployment)
- [API Endpoints](#api-endpoints)
- [Docker Optimization Results](#docker-optimization-results)
  - [Key Optimization Techniques](#key-optimization-techniques)
- [CRaC for Spring Boot Application](#crac-for-spring-boot-application)
  - [What is CRaC?](#what-is-crac)
  - [Benefits of CRaC](#benefits-of-crac)
  - [Implementation Requirements](#implementation-requirements)
  - [Using CRaC with Docker](#using-crac-with-docker)
- [CDS for Spring Boot Application](#cds-for-spring-boot-application)
  - [What is Class Data Sharing (CDS)?](#what-is-class-data-sharing-cds)
  - [Using CDS with Docker](#using-cds-with-docker)
  - [Building and Running with CDS](#building-and-running-with-cds)
- [CI/CD with GitHub Actions](#cicd-with-github-actions)
- [Project Structure](#project-structure)
- [Usage Examples](#usage-examples)
  - [Testing the API with curl](#testing-the-api-with-curl)
  - [Running with Different Docker Images](#running-with-different-docker-images)
- [License](#license)

## Overview

This project demonstrates DevOps practices and Docker optimization techniques for a Spring Boot 3.5.4 application running on Java 21. It serves as a research and development project to explore various containerization approaches and their impact on final image size.

The application provides simple REST API endpoints for testing and demonstration purposes.

## Technologies Used

- Java 21
- Spring Boot 3.5.4
- Maven
- Docker
- BellSoft Liberica JDK/JRE
- Eclipse Temurin JDK/JRE

## Prerequisites

- Java 21 or later
- Maven 3.6+
- Docker

## Setup and Installation

### Local Development

1. Clone the repository: 
    ```
   git clone https://github.com/fahimfahad92/java-21-spring-boot-3-devops.git
   ```
2. Build the project:
   ```bash
   ./mvnw clean package
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Docker Deployment

The project includes multiple Dockerfiles demonstrating different approaches to containerization:

1. Single-stage build with Eclipse Temurin:
   ```bash
   docker build -f Dockerfile-eclipse-temurin-single-stage -t spring-app:temurin-single .
   ```

2. Multi-stage build with Eclipse Temurin:
   ```bash
   docker build -f Dockerfile-eclipse-temurin-multi-stage -t spring-app:temurin-multi .
   ```

3. Single-stage build with BellSoft Liberica:
   ```bash
   docker build -f Dockerfile-liberica-single-stage -t spring-app:liberica-single .
   ```

4. Multi-stage build with BellSoft Liberica (the smallest image):
   ```bash
   docker build -f Dockerfile-liberica-multi-stage -t spring-app:liberica-multi .
   ```

5. Run the container:
   ```bash
   docker run -p 8080:8080 spring-app:liberica-multi
   ```

## API Endpoints

The application exposes two simple REST endpoints:

1. **GET /ping**
   - Returns: `{"message": "pong at [current timestamp]"}`
   - Purpose: Simple health check endpoint

2. **GET /welcome**
   - Returns: `{"message": "Welcome to Spring Boot 3.5 at [current timestamp]"}`
   - Purpose: Welcome message endpoint

## Docker Optimization Results

This project explores different Docker base images and build strategies to optimize the final container size. The table below shows the comparison of different approaches:

| Image Name | Base Image | Build Strategy | Final Image Size | After using docker slim | Notes |
|------------|------------|----------------|-----------------|-----------------|-------|
| eclipse-temurin | 21-jdk-alpine | Single-stage | 738.76 MB | 299.32 MB | Largest image size, includes full JDK |
| eclipse-temurin | 21-jdk-alpine (build)<br>21-jre-alpine (runtime) | Multi-stage | 321.53 MB | 239.79 MB | 56% smaller than single-stage |
| bellsoft/liberica-runtime-container | jdk-21-musl | Single-stage | 387.25 MB | 198.58 MB | Smaller than Temurin single-stage |
| bellsoft/liberica-runtime-container | jdk-21-musl (build)<br>jre-21-slim-musl (runtime) | Multi-stage | 219.98 MB | 214.37MB | Smallest image, 70% reduction from largest |

### Key Optimization Techniques

1. **Multi-stage builds**: Separating the build environment from the runtime environment
2. **JRE instead of JDK**: Using smaller JRE images for runtime
3. **Layer optimization**: Extracting and organizing application into layers
4. **Alpine/Musl-based images**: Using lightweight base images
5. **Cleanup**: Removing unnecessary build artifacts and cache
6. **Docker slim**: Using docker slim can reduce image size significantly, but it requires caution

## CRaC for Spring Boot Application

### What is CRaC?

CRaC (Coordinated Restore at Checkpoint) is a technology that significantly reduces application startup time by creating a checkpoint of a running application and later restoring it from that checkpoint. This is particularly beneficial for Java applications, which traditionally have longer startup times compared to other technologies.

### Benefits of CRaC

- **Dramatically reduced startup times**: Applications can start in milliseconds instead of seconds or minutes
- **Warm application state**: The application is restored with a warm state, including initialized caches and connections
- **Lower resource usage during startup**: Less CPU and memory consumption during the restore phase
- **Improved user experience**: Faster application availability after deployment or scaling events

### Implementation Requirements

To use CRaC with Spring Boot, you need:

1. Add the CRaC dependency to your pom.xml:

```xml
<dependency>
    <groupId>org.crac</groupId>
    <artifactId>crac</artifactId>
    <version>1.4.0</version>
</dependency>
```

2. Use a JDK/JRE that supports CRaC (like BellSoft Liberica JDK/JRE with CRaC support)
3. Configure your application to create checkpoints at appropriate times

### Using CRaC with Docker

This project includes a specialized Dockerfile (`Dockerfile-liberica-crac`) that sets up the environment for CRaC. The following steps demonstrate how to build, checkpoint, and restore the application:

```bash
# Step 1: Build the Docker image with CRaC support
docker build . -t demo-app-crac -f Dockerfile-liberica-crac

# Step 2: Run the container with required capabilities
# This will start the application and create a checkpoint
docker run --cap-add CHECKPOINT_RESTORE --cap-add SYS_PTRACE --name demo-app-crac demo-app-crac

# Step 3: Create a new image from the container with the checkpoint
# This image will be configured to restore from the checkpoint on startup
docker commit --change='ENTRYPOINT ["java", "-XX:CRaCRestoreFrom=/checkpoint"]' demo-app-crac demo-app-crac-restore

# Step 4: Remove the original container
docker rm -f demo-app-crac

# Step 5: Run the application from the checkpoint
# Notice how quickly the application starts up!
docker run -it --rm -p 8080:8080 --cap-add CHECKPOINT_RESTORE --cap-add SYS_PTRACE --name demo-app-crac --entrypoint java demo-app-crac-restore -XX:CRaCRestoreFrom=/checkpoint
```

## CDS for Spring Boot Application

### What is Class Data Sharing (CDS)?

Class Data Sharing (CDS) is a JVM feature that improves startup time and reduces memory footprint by:
- Pre-processing and storing class metadata in an archive file
- Sharing this metadata across multiple JVM instances
- Reducing the time needed for class loading during application startup

CDS is particularly beneficial for containerized Spring Boot applications, as it can significantly reduce startup times in environments where quick scaling is important.

### Prerequisites

1. Enable AOT (Ahead-of-Time) processing in pom.xml:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>process-aot</id>
            <goals>
                <goal>process-aot</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

2. Use a JDK/JRE that supports CDS (like BellSoft Liberica JDK/JRE with CDS support)

### Using CDS with Docker

This project includes a specialized Dockerfile (`Dockerfile-liberica-cds`) that sets up the environment for CDS. The key components of this Dockerfile are:

1. Using BellSoft Liberica JRE with CDS support
2. Creating a CDS archive during container build with:
   ```
   java -XX:ArchiveClassesAtExit=./application.jsa -Dspring.context.exit=onRefresh -jar app.jar
   ```
3. Configuring the application to use the CDS archive at startup with:
   ```
   -XX:SharedArchiveFile=application.jsa
   ```

### Building and Running with CDS

```bash
# Step 1: Build the Docker image with CDS support
docker build . -t demo-app-liberica-cds -f Dockerfile-liberica-cds

# Step 2: Run the container
# Notice the improved startup time compared to non-CDS containers
docker run -p 8080:8080 --name demo demo-app-liberica-cds
```

## CI/CD with GitHub Actions

This repository includes comprehensive GitHub Actions workflows to automate container builds, image publishing to Amazon ECR, and deployments to both Amazon ECS and EKS. All workflows are triggered on push events to the master branch and use GitHub OIDC to assume AWS IAM roles without storing long-lived AWS credentials.

### Available Workflows

#### 1. Push to ECR (`ci-dev.yml`)
**Purpose**: Basic Docker image build and push to Amazon ECR without optimization features.

**Trigger**: Push to `master` branch

**Key Features**:
- Uses GitHub OIDC for secure AWS authentication
- Builds using `Dockerfile-liberica-multi-stage` for optimal image size
- Tags and pushes as `demo-app:latest` to ECR

**Workflow Steps**:
1. **Checkout**: Uses `actions/checkout@v4` to fetch repository code
2. **AWS Authentication**: Uses `aws-actions/configure-aws-credentials@v4` with OIDC to assume the `GHA-ECR-PUSH` role in region `ap-southeast-1`
3. **ECR Login**: Uses `aws-actions/amazon-ecr-login@v2` to authenticate with Amazon ECR
4. **Build**: Executes `docker build . -t demo-app -f Dockerfile-liberica-multi-stage`
5. **Push**: Tags and pushes the image to ECR registry as `demo-app:latest`

#### 2. Push to ECR with Cache (`ci-cache-dev.yml`)
**Purpose**: Optimized Docker image build with GitHub Actions caching to speed up subsequent builds.

**Trigger**: Push to `master` branch

**Key Features**:
- Docker Buildx for advanced build features
- GitHub Actions cache integration for Docker layers
- Concurrency control to prevent conflicting builds
- Multiple image tagging (SHA and latest)

**Environment Variables**:
- `AWS_REGION`: ap-southeast-1
- `ECR_REPO`: demo-app
- `ECS_CLUSTER`: demo-app-cluster
- `ECS_SERVICE`: demo-app-service
- `DOCKER_FILE`: Dockerfile-liberica-multi-stage
- `IAM_ROLE`: arn:aws:iam::568560058349:role/GH-ECS-DEPLOY

**Workflow Steps**:
1. **Checkout**: Repository code checkout
2. **AWS Authentication**: OIDC-based authentication using the `GH-ECS-DEPLOY` role
3. **ECR Login**: Amazon ECR authentication
4. **Docker Buildx Setup**: Uses `docker/setup-buildx-action@v3` for advanced build capabilities
5. **Cached Build**: Uses `docker/build-push-action@v6` with GitHub Actions cache (`cache-from: type=gha`, `cache-to: type=gha,mode=max`)
6. **Multi-tag Push**: Pushes both SHA-based and latest tags to ECR

#### 3. Deploy to ECS (`ci-deploy-to-ecs-dev.yml`)
**Purpose**: Complete CI/CD pipeline that builds, pushes, and deploys to Amazon ECS with service stability verification.

**Trigger**: Push to `master` branch

**Key Features**:
- End-to-end deployment to Amazon ECS
- Service stability verification
- Concurrency control with `cancel-in-progress: true`
- Force deployment to ensure latest image is used

**Workflow Steps**:
1. **Checkout**: Repository code checkout
2. **AWS Authentication**: OIDC authentication using `GH-ECS-DEPLOY` role
3. **ECR Login**: Amazon ECR authentication
4. **Docker Buildx Setup**: Advanced Docker build setup
5. **Cached Build**: Optimized build with GitHub Actions caching
6. **Multi-tag Push**: Pushes images with both commit SHA and latest tags
7. **ECS Deployment**: Executes `aws ecs update-service --force-new-deployment` to trigger rolling update
8. **Stability Check**: Uses `aws ecs wait services-stable` to ensure deployment completes successfully

#### 4. Deploy to EKS (`ci-deploy-to-eks-dev.yml`)
**Purpose**: Complete CI/CD pipeline for Kubernetes deployment on Amazon EKS with manifest updates and rollout verification.

**Trigger**: Push to `master` branch

**Key Features**:
- Kubernetes deployment to Amazon EKS
- Dynamic image tag updates in Kubernetes manifests
- Rollout status verification
- Docker layer caching optimization

**Environment Variables**:
- `AWS_REGION`: ap-southeast-1
- `ECR_REPO`: demo-app
- `DOCKER_FILE`: Dockerfile-liberica-multi-stage
- `IAM_ROLE`: arn:aws:iam::568560058349:role/GH-EKS-DEPLOY
- `CLUSTER_NAME`: demo-app-cluster
- `NAMESPACE`: default

**Workflow Steps**:
1. **Checkout**: Repository code checkout
2. **AWS Authentication**: OIDC authentication using `GH-EKS-DEPLOY` role
3. **ECR Login**: Amazon ECR authentication
4. **Docker Buildx Setup**: Advanced Docker build capabilities
5. **Cache Management**: Uses `actions/cache@v4` for Docker layer caching with `/tmp/.buildx-cache`
6. **Short SHA Generation**: Creates a 7-character commit SHA for image tagging
7. **Cached Build**: Builds image with local cache optimization
8. **ECR Push**: Pushes image tagged with commit SHA
9. **Kubectl Configuration**: Updates kubeconfig using `aws eks update-kubeconfig`
10. **Manifest Update**: Uses `sed` to update the image reference in `k8s/demo-app.yaml`
11. **Kubernetes Apply**: Applies updated manifests using `kubectl apply`
12. **Cache Rotation**: Moves new cache to replace old cache for next build
13. **Rollout Verification**: Uses `kubectl rollout status` with 180-second timeout

### Prerequisites and Setup

**AWS Infrastructure Requirements**:
- **IAM Roles**: 
  - `GHA-ECR-PUSH`: Permissions for ECR push operations
  - `GH-ECS-DEPLOY`: Permissions for ECR push and ECS deployment
  - `GH-EKS-DEPLOY`: Permissions for ECR push and EKS deployment
- **ECR Repository**: `demo-app` must exist in the target AWS account/region
- **ECS Infrastructure**: 
  - Cluster: `demo-app-cluster`
  - Service: `demo-app-service` (must reference the ECR image `demo-app:latest`)
- **EKS Infrastructure**:
  - Cluster: `demo-app-cluster`
  - Kubernetes manifests in `k8s/` directory

**GitHub Configuration**:
- OIDC provider configured for GitHub Actions
- Repository secrets and variables configured for AWS authentication
- Branch protection rules for `master` branch (optional but recommended)

**Dockerfile Standards**:
All workflows use `Dockerfile-liberica-multi-stage` by default for optimal image size and performance.

**Manual Trigger Configuration**:
To enable manual workflow triggers, add the following to any workflow file:

- Push to ECR (.github/workflows/ci-dev.yml)
  - Trigger: push on master
  - Purpose: Build the Docker image and push demo-app:latest to Amazon ECR
  - Key steps:
    - actions/checkout@v4
    - aws-actions/configure-aws-credentials@v4 (assume role arn:aws:iam::568560058349:role/GHA-ECR-PUSH; region ap-southeast-1)
    - aws-actions/amazon-ecr-login@v2
    - docker build -f Dockerfile-liberica-multi-stage -t demo-app:latest .
    - docker tag and docker push to <account>.dkr.ecr.ap-southeast-1.amazonaws.com/demo-app:latest

- Push to ECR with cache (.github/workflows/ci-cache-dev.yml)
  - Trigger: push on master
  - Purpose: Same as above but optimized with Docker Buildx and a persistent cache to speed up subsequent builds
  - Key steps:
    - actions/checkout@v4
    - aws-actions/configure-aws-credentials@v4 (role: GHA-ECR-PUSH)
    - aws-actions/amazon-ecr-login@v2
    - docker/setup-buildx-action@v3
    - actions/cache@v4 for /tmp/.buildx-cache
    - docker/build-push-action@v6 with cache-from/cache-to, load: true, tags: <ECR_REGISTRY>/demo-app:latest
    - docker push

- Deploy to ECS (.github/workflows/ci-deploy-to-ecs-dev.yml)
  - Trigger: push on master
  - Purpose: Build and push the image, then trigger a new deployment on Amazon ECS
  - Environment variables:
    - AWS_REGION: ap-southeast-1
    - ECR_REPO: demo-app
    - ECS_CLUSTER: demo-app-cluster
    - ECS_SERVICE: demo-app-service
    - DOCKER_FILE: Dockerfile-liberica-multi-stage
    - IAM_ROLE: arn:aws:iam::568560058349:role/GH-ECS-DEPLOY
  - Key steps:
    - actions/checkout@v4
    - aws-actions/configure-aws-credentials@v4 (assume IAM_ROLE via OIDC)
    - aws-actions/amazon-ecr-login@v2
    - docker/setup-buildx-action@v3 + actions/cache@v4 for layer caching
    - docker/build-push-action@v6 (build) and docker push to ECR
    - aws ecs update-service --cluster $ECS_CLUSTER --service $ECS_SERVICE --force-new-deployment

Notes and prerequisites
- AWS: The referenced IAM roles (GHA-ECR-PUSH and GH-ECS-DEPLOY) must exist with permissions to push to ECR and deploy to ECS respectively.
- ECR repository: demo-app must exist in the target AWS account/region.
- ECS: demo-app-cluster and demo-app-service should already be provisioned; the service must reference the ECR image demo-app:latest.
- Dockerfile: All workflows build using Dockerfile-liberica-multi-stage by default.
- Manual runs: These workflows are configured for push events. If you want manual triggers, add a workflow_dispatch block to the respective YAML files.

## Project Structure

```
java-21-spring-boot-3-devops/
├── Dockerfile-eclipse-temurin-multi-stage
├── Dockerfile-eclipse-temurin-single-stage
├── Dockerfile-liberica-multi-stage
├── Dockerfile-liberica-single-stage
├── src/
│   ├── main/
│   │   ├── java/rnd/fahim/java21springboot3devops/
│   │   │   ├── Java21SpringBoot3DevopsApplication.java
│   │   │   ├── ResponseDto.java
│   │   │   └── TestController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/rnd/fahim/java21springboot3devops/
│           └── Java21SpringBoot3DevopsApplicationTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Usage Examples

### Testing the API with curl

```bash
# Test ping endpoint
curl http://localhost:8080/ping

# Test welcome endpoint
curl http://localhost:8080/welcome
```

### Running with Different Docker Images

```bash
# Run with the smallest image (Liberica multi-stage)
docker run -p 8080:8080 spring-app:liberica-multi

# Run with Temurin single-stage image
docker run -p 8080:8080 spring-app:temurin-single
```

## License

This project is created for research and development purposes.

---

Last updated: August 31, 2025