FROM eclipse-temurin:21-jre

WORKDIR /app

# créer un user non-root
RUN useradd -m appuser
USER appuser

# copie le jar Spring Boot
COPY target/*SNAPSHOT.jar app.jar

EXPOSE 8080

# JVM options
ENTRYPOINT ["java","-jar","/app/app.jar"]
