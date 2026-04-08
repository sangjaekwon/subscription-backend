FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY ../build/libs/*.jar /app/app.jar

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
