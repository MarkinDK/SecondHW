FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
COPY target/SecondHW-0.0.1-SNAPSHOT.jar SecondHW.jar
ENTRYPOINT ["java","-jar","/SecondHW.jar"]