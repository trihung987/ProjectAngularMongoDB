FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Build the application first with: mvn clean package
# Then run with Docker Compose: docker-compose up --build