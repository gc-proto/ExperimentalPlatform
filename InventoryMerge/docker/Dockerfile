FROM maven:3.5-jdk-8
RUN apt-get clean
RUN apt-get update
RUN mkdir -p /app
COPY export/ /app/export/
COPY data/ /app/data/
COPY secrets/ /app/secrets/
COPY tokens/ /app/tokens/
RUN mkdir /app/import/
ADD target/InventoryCreator-1.0.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]

