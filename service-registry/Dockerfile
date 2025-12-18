FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
COPY target/*.jar app.jar
COPY wait-for-it.sh .
RUN chmod +x wait-for-it.sh
ENTRYPOINT ["./wait-for-it.sh", "config-server", "8888", "java", "-jar", "app.jar"]