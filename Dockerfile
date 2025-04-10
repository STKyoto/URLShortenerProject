FROM gradle:8.13-jdk21 AS build

WORKDIR /app

COPY --chown=gradle:gradle . /app

RUN gradle build --no-daemon

FROM openjdk:21 AS runtime

WORKDIR /app

COPY --from=build /app/build/libs/demo-0.0.1-SNAPSHOT.jar /app/link-transformation-project.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "link-transformation-project.jar"]
