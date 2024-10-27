FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY src src

RUN mvn clean package

FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY --from=build /workspace/app/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
