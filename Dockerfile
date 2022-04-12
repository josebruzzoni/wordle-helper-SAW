# syntax=docker/dockerfile:1
# Building the application inside an alpine container
FROM openjdk:16-alpine3.13
# Defining the working directory
WORKDIR /wordle-helper
# We copy the mvnw and pom.xml files and also the .mvn folder to the image
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Install dos2unix package to convert file line ending from CRLF to LF
RUN apk add dos2unix
RUN dos2unix mvnw
# We install the dependencies
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
# We now copy the source files
COPY src ./src
# We run the Springboot application inside the image
CMD ["./mvnw", "spring-boot:run"]