FROM eclipse-temurin:26-jdk AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY auth-service/build.gradle build.gradle
COPY auth-service/settings.gradle settings.gradle
COPY auth-service/src src
RUN chmod +x gradlew && ./gradlew bootJar -x test

FROM eclipse-temurin:26-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
