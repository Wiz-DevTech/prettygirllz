FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/frontend-gateway-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
LABEL authors="Wiz"
