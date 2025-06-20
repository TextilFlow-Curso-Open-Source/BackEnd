# Usar imagen oficial con Maven incluido
FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

# Copiar archivos
COPY pom.xml .
COPY src ./src

# Build
RUN mvn clean package -DskipTests

EXPOSE 8080

# JVM optimizada
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/*.jar"]