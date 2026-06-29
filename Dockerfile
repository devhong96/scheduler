FROM amazoncorretto:25
COPY ./build/libs/scheduler.jar scheduler.jar
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-jar", "scheduler.jar"]