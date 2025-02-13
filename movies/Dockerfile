FROM maven:3.9.9-eclipse-temurin-23 AS builder

ARG APP_DIR=/myapp

WORKDIR ${APP_DIR}

COPY src src
COPY .mvn .mvn
COPY pom.xml .
COPY mvnw .
COPY movies_post_2010.zip .

RUN mvn clean package -Dmaven.test.skip=true

FROM maven:3.9.9-eclipse-temurin-23

COPY --from=builder /myapp/target/movies-0.0.1-SNAPSHOT.jar app.jar
COPY director_movies_report.jrxml .

ENV SERVER_PORT=8080

EXPOSE ${SERVER_PORT}

ENTRYPOINT java -jar app.jar