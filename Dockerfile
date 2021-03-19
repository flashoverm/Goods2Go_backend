FROM openjdk:9.0.4-jre-slim

RUN mkdir /workspace
WORKDIR /workspace
ADD ./target/ /workspace

CMD ["java","-jar","g2gv2-0.0.1-SNAPSHOT.jar","--spring.config.name=application-kubernetes"]