# Usar imagen más pequeña y existente
FROM maven:3.9-openjdk-17-slim AS builder

WORKDIR /app

# Copiar solo pom.xml primero para cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Build con menos memoria
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Imagen final más pequeña
FROM openjdk:17-jre-slim

WORKDIR /app

# Copiar solo el JAR
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Configurar JVM para poca memoria
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]