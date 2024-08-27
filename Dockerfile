# Use a Maven image to build the project
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and any other files necessary to build the project
COPY pom.xml .

# Download all the dependencies
RUN mvn dependency:go-offline -B

# Copy the rest of the project
COPY service-client/src ./src

# Build the project and create the JAR file
RUN mvn clean install -DskipTests

# Use a smaller image to run the application
FROM eclipse-temurin:22-jdk-alpine

# Set the location of the JAR file as a build argument
ARG JAR_FILE=service-client/target/service-client-0.0.1-SNAPSHOT.jar

# Copy the JAR file from the previous stage
COPY --from=build /app/${JAR_FILE} app.jar

# Install font packages (if required by your application)
RUN apk add --no-cache fontconfig ttf-dejavu

# Expose the port your application will run on
EXPOSE 8080

# Define the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
