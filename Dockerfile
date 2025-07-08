# Etapa de build
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw .
COPY pom.xml .
COPY src/ src/

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa final
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/ExtractPDF-.1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
