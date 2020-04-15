FROM ubuntu:18.04
RUN apt-get update
RUN yes | apt-get install openjdk-8-jdk
RUN yes | apt-get install openjfx
RUN yes | apt-get purge openjfx
RUN yes | apt-get install openjfx=8u161-b12-1ubuntu2 libopenjfx-jni=8u161-b12-1ubuntu2 libopenjfx-java=8u161-b12-1ubuntu2
RUN yes | apt-mark hold openjfx libopenjfx-jni libopenjfx-java

# The application's jar file
ARG JAR_FILE=./docker/images/pageperformance_cacher/PagePerformanceCacher-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} PagePerformanceCacher.jar









