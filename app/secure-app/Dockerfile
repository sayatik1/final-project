FROM eclipse-temurin:17-jdk

RUN useradd -m springuser

WORKDIR /app

COPY target/*.jar app.jar

RUN chown -R springuser:springuser /app

USER springuser

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]