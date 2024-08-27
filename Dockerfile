FROM eclipse-temurin:22-jdk-alpine

# Argument for the JAR file location
ARG JAR_FILE=service-client/target/service-client-0.0.1-SNAPSHOT.jar

# Copy the JAR file into the Docker image
COPY ${JAR_FILE} app.jar

# Install font packages (if required by your application)
RUN apk add --no-cache fontconfig ttf-dejavu

# Expose the port your application will run on
EXPOSE 8080

# Define the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]