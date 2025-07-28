# Java 21 Spring Boot 3 DevOps

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-green)
![Docker](https://img.shields.io/badge/Docker-Optimized-blue)

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

| Image Name | Base Image | Build Strategy | Final Image Size | Notes |
|------------|------------|----------------|-----------------|-------|
| eclipse-temurin | 21-jdk-alpine | Single-stage | 738.76 MB | Largest image size, includes full JDK |
| eclipse-temurin | 21-jdk-alpine (build)<br>21-jre-alpine (runtime) | Multi-stage | 321.53 MB | 56% smaller than single-stage |
| bellsoft/liberica-runtime-container | jdk-21-musl | Single-stage | 387.25 MB | Smaller than Temurin single-stage |
| bellsoft/liberica-runtime-container | jdk-21-musl (build)<br>jre-21-slim-musl (runtime) | Multi-stage | 219.98 MB | Smallest image, 70% reduction from largest |

### Key Optimization Techniques

1. **Multi-stage builds**: Separating the build environment from the runtime environment
2. **JRE instead of JDK**: Using smaller JRE images for runtime
3. **Layer optimization**: Extracting and organizing application into layers
4. **Alpine/Musl-based images**: Using lightweight base images
5. **Cleanup**: Removing unnecessary build artifacts and cache

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

Last updated: July 28, 2025