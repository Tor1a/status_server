FROM ubuntu:22.04

ENV APP_HOME=/apps

ARG JAR_FILE_PATH=build/libs/*.jar

WORKDIR $APP_HOME

COPY $JAR_FILE_PATH status_server.jar

EXPOSE 8080

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk libc6

CMD ["java", "-jar", "status_server.jar"]