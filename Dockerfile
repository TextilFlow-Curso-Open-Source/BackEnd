# Multi-stage build for Spring Boot
FROM maven:3.9.6-openjdk-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stages
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application :3
ENTRYPOINT ["java", "-jar", "app.jar"]