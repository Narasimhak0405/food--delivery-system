# ==============================================================================
# FoodieExpress - Production Multi-Stage Dockerfile
# Stage 1: Build Java artifact using Maven & OpenJDK 17
# Stage 2: Minimal Distroless / Alpine Runtime
# ==============================================================================

FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy Maven descriptor and source code
COPY pom.xml .
COPY src ./src

# Install Maven and build clean executable JAR package
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# Stage 2: Runtime Container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root system user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built JAR from builder stage
COPY --from=builder /app/target/foodie-express-*.jar app.jar

# Expose Spring Boot web port
EXPOSE 8080

# Run JVM with container memory awareness flags
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Dspring.profiles.active=prod", "-jar", "app.jar"]

