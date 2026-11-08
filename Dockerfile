# 1. Etapa de compilación con Gradle y JDK 21
FROM public.ecr.aws/docker/library/gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Permisos de ejecución
RUN chmod +x gradlew

RUN ./gradlew buildFatJar --no-daemon || ./gradlew build --no-daemon

# 2. Etapa de ejecución con Java 21
FROM public.ecr.aws/docker/library/openjdk:21-slim
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar /app/pizzeria-backend.jar
ENTRYPOINT ["java", "-jar", "/app/pizzeria-backend.jar"]