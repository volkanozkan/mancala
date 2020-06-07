FROM java:8-jdk-alpine

WORKDIR /home/java

EXPOSE 9000

ENTRYPOINT ["java","-jar","volkan-mancala.jar"]

COPY /target/volkanozkan-*.jar volkan-mancala.jar
