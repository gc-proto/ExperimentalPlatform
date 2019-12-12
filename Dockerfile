FROM ubuntu:18.04
RUN apt-get update
RUN yes | apt-get upgrade
RUN yes | apt-get install git
RUN yes | apt-get install curl
RUN yes | curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl
RUN yes | chmod +x ./kubectl
RUN yes | mv ./kubectl /usr/local/bin/kubectl
RUN yes | curl -sL https://aka.ms/InstallAzureCLIDeb | bash
RUN curl https://get.helm.sh/helm-v2.14.3-linux-amd64.tar.gz --output helm.tar.gz
RUN tar -xvzf helm.tar.gz
RUN chmod +x ./linux-amd64/helm
RUN mv ./linux-amd64/helm /usr/local/bin/helm
COPY ./secrets/init.sh /
RUN chmod +x init.sh
RUN /init.sh
RUN az aks get-credentials --resource-group tbstest-aks --name tbstest-aks --admin --overwrite-existing
RUN helm init --service-account tiller
RUN rm helm.tar.gz
# Install OpenJDK-8
RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y ant && \
    apt-get clean;

# Fix certificate issues
RUN apt-get update && \
    apt-get install ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f;
# Make port 8080 available to the world outside this container
EXPOSE 8888


RUN mkdir /home/requests
RUN mkdir /home/secrets
COPY ./secrets/notification.key /home/secrets
COPY ./helm-drupal /home/helm-drupal

# The application's jar file
ARG JAR_FILE=./EPRequest/target/EPRequest-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} EPRequest.jar

EXPOSE 8000

RUN yes | apt-get install dnsutils

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n
# Run the jar file 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/EPRequest.jar"]



