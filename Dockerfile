FROM maven:3.9.16-eclipse-temurin-21-alpine as BUILDER
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -DskipTests

COPY src ./src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre as RUUNER

WORKDIR /app

COPY --from=BUILDER /app/target/*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]