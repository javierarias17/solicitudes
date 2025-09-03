FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY applications/app-service/build/libs/solicitudes.jar ./solicitudes.jar
ENTRYPOINT [ "java", "-jar", "solicitudes.jar" ]
