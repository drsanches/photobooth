FROM openjdk:11

ADD ./app/build/libs/photobooth-1.0.jar /app/
CMD ["java", "-jar", "/app/photobooth-1.0.jar"]

EXPOSE ${PORT}