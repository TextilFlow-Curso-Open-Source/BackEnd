# Usar imagen con Java 24
FROM maven:3.9.9-eclipse-temurin-24

WORKDIR /app

# Copiar pom.xml primero para cache de dependencias
COPY pom.xml .

# Descargar dependencias primero
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Build
RUN mvn clean package -DskipTests

EXPOSE 8080

# JVM optimizada para poca memoria
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/*.jar"]