FROM openjdk:11

ENV JAVA_OPTS="-Dvaadin.productionMode=true -Duser.timezone=CET"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
EXPOSE 8080 8081
VOLUME /tmp

ADD ./target/*.jar app.jar
RUN sh -c 'touch /app.jar'