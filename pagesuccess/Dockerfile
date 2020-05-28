FROM maven:3.5-jdk-8
RUN apt-get update
RUN yes | apt-get install python
RUN yes | apt-get install python3-pandas
RUN yes | apt-get install python3-matplotlib
RUN yes | apt-get install python3-nltk
COPY python/ /tmp/
RUN mkdir -p /build
WORKDIR /build
COPY pom.xml /build
#Download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins
#Copy source code
COPY src /build/src
# Build application
RUN mvn package
RUN mkdir -p /app
RUN cp /build/target/PageSuccess-0.0.1-SNAPSHOT.jar /app/app.jar
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n
ENTRYPOINT ["java","-jar","/app/app.jar"]

