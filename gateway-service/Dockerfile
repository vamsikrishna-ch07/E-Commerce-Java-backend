# Use a robust and widely-used OpenJDK 17 image
FROM eclipse-temurin:17-jdk-focal

# Set the working directory in the container
WORKDIR /app

# Copy the compiled Spring Boot jar file into the container
COPY target/*.jar app.jar

# Expose the port defined in docker-compose.yml (8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
