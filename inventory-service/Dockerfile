# Use lightweight JRE image
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy built jar
COPY target/inventory-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port (match your app)
EXPOSE 8082

# Run app
ENTRYPOINT ["java","-jar","/app.jar"]