# 1. Etapa de compilación con mirror de AWS
FROM public.ecr.aws/docker/library/gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# Conceder permisos de ejecución al script de Gradle
RUN chmod +x gradlew

RUN ./gradlew buildFatJar --no-daemon || ./gradlew build --no-daemon

# 2. Etapa de ejecución
FROM public.ecr.aws/docker/library/openjdk:17-slim
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar /app/pizzeria-backend.jar
ENTRYPOINT ["java", "-jar", "/app/pizzeria-backend.jar"]