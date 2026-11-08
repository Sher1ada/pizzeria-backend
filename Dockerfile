FROM gradle:8-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew buildFatJar --no-daemon || ./gradlew build --no-daemon

FROM openjdk:17-slim
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar /app/pizzeria-backend.jar
ENTRYPOINT ["java", "-jar", "/app/pizzeria-backend.jar"]