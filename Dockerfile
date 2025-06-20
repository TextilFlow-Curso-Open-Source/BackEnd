# Usar imagen oficial de Maven
FROM maven:3.8.6-openjdk-17 AS builder

WORKDIR /app

# Copiar pom.xml para cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Build con menos memoria y sin tests
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Imagen final - usar la imagen oficial de OpenJDK
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar JAR construido
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# JVM optimizada para poca memoria
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxMetaspaceSize=128m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]