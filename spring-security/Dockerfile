# Use a robust and widely-used OpenJDK 17 image
FROM eclipse-temurin:17-jdk-focal

# Set the working directory in the container
WORKDIR /app

# Copy the compiled Spring Boot jar file into the container
# Explicitly copy the generated JAR file name
COPY target/spring-security-1.0.jar app.jar

# Expose the port defined in docker-compose.yml (SERVER_PORT=8089)
EXPOSE 8089

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
