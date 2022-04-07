# syntax=docker/dockerfile:1
# Building the application inside an alpine container
FROM openjdk:16-alpine3.14
# Defining the working directory
WORKDIR /wordle-helper
# We copy the mvnw and pom.xml files and also the .mvn folder to the image
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# We install the dependencies
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
# We now copy the source files
COPY src ./src
# We run the Springboot application inside the image
CMD ["./mvnw", "spring-boot:run"]