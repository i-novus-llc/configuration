FROM harbor.i-novus.ru/library/redos7c/jdk-21:21.0.5-1.30.0

EXPOSE 8080

ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar","--server.port=8080"]
