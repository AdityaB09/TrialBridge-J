# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests clean package

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/trialbridge-api-0.0.1-SNAPSHOT.jar app.jar
ENV PORT 8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
