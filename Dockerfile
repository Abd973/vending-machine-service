# Use a base Java image
FROM eclipse-temurin:17

# Create app directory
WORKDIR /app

# Copy the Spring Boot jar
COPY target/vending-machine-1.0.0.jar app.jar

# Expose the port Spring Boot will run on
EXPOSE 8080


# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]