FROM eclipse-temurin:21-jre
ARG JAR_FILE=target/fondaSpringUnirF-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app_servidor_fonda.jar
EXPOSE 3010
ENTRYPOINT ["java", "-jar", "app_servidor_fonda.jar"]