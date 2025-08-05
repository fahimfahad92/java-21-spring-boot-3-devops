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