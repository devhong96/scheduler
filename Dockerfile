FROM openjdk:17-jdk
COPY ./build/libs/scheduler.jar scheduler.jar
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-jar", "scheduler.jar"]