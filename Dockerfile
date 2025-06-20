# Usar solo OpenJDK e instalar Maven manualmente
FROM openjdk:17

WORKDIR /app

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copiar archivos
COPY pom.xml .
COPY src ./src

# Build
RUN mvn clean package -DskipTests

EXPOSE 8080

# JVM optimizada
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/*.jar"]