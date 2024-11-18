FROM ghcr.io/graalvm/jdk-community:21
LABEL author="Adam Rozycki"

COPY ./inbox-infra/target/inbox-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]