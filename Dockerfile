# Multi-stage Dockerfile untuk Gemini Chatbot CLI
# Stage 1: Build stage dengan Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml terlebih dahulu untuk caching dependency layer
COPY pom.xml .

# Download dependencies (ini akan di-cache jika pom.xml tidak berubah)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build aplikasi dan skip tests untuk build yang lebih cepat
RUN mvn clean package -DskipTests=true

# Copy dependencies ke folder dependency
RUN mvn dependency:copy-dependencies

# Stage 2: Runtime stage dengan JRE minimal
FROM eclipse-temurin:17-jre-alpine AS runtime

# Install packages yang dibutuhkan untuk CLI interactivity
RUN apk add --no-cache \
    bash \
    ncurses \
    && rm -rf /var/cache/apk/*

# Create app user untuk security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy JAR files dari build stage
COPY --from=builder /app/target/classes ./classes
COPY --from=builder /app/target/dependency ./lib

# Copy entry point script
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Create directory untuk .env file
RUN mkdir -p /app/config

# Set ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    APP_ENV="production" \
    JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD java -cp "/app/classes:/app/lib/*" com.juaracoding.cicd.Main --health-check || exit 1

# Expose no ports (CLI application)
# EXPOSE none

# Set entry point
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

# Default command
CMD ["java", "-cp", "/app/classes:/app/lib/*", "com.juaracoding.cicd.Main"]
