# Stage 1: Build the app
FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy only pom.xml first to leverage dependency cache
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download dependencies (this layer will be cached if pom.xml hasn't changed)
RUN mvn dependency:go-offline

# Now copy the rest of the source code
COPY src ./src

# Then build
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]