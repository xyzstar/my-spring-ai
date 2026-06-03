FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR file
COPY my-spring-ai-1.0-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8000/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
