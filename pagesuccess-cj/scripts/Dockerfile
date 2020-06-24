FROM maven:3.5-jdk-8
RUN apt-get clean
RUN apt-get update
RUN mkdir -p /app
ADD target/pagesuccess-cj-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]

