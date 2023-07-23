FROM openjdk:11

ADD build/libs/photobooth-1.0.jar /app/
CMD ["java", "-jar", "/app/photobooth-1.0.jar"]

EXPOSE ${APP_PORT}
HEALTHCHECK --interval=5m --timeout=3s CMD curl -f http://localhost:${APP_PORT}/actuator/health || exit 1
