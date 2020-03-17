FROM ubuntu:18.04
RUN apt-get update
RUN yes | apt-get upgrade
RUN yes | apt-get install dnsutils
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
RUN yes | apt-get install git
RUN yes | apt-get install curl
RUN yes | apt-get install wget
RUN yes | curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl
RUN yes | chmod +x ./kubectl
RUN yes | mv ./kubectl /usr/local/bin/kubectl
RUN yes | curl -sL https://aka.ms/InstallAzureCLIDeb | bash
RUN curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3
RUN chmod 700 get_helm.sh
RUN ./get_helm.sh
RUN rm ./get_helm.sh
RUN wget https://github.com/vmware-tanzu/velero/releases/download/v1.2.0/velero-v1.2.0-linux-amd64.tar.gz
RUN tar -zxvf velero-v1.2.0-linux-amd64.tar.gz
RUN mv velero-v1.2.0-linux-amd64/velero /usr/local/bin/
COPY ./velero/schedule-namespace.sh /home/velero/
RUN mkdir /home/secrets
COPY ./secrets/init.sh /home/secrets/
RUN chmod +x /home/secrets/init.sh
RUN ./home/secrets/init.sh
ARG CLUSTER_ENV
RUN echo ARG:${CLUSTER_ENV}
RUN az aks get-credentials --resource-group ${CLUSTER_ENV}-rg --name ${CLUSTER_ENV}-aks --admin --overwrite-existing
RUN mkdir /home/requests
COPY ./secrets/notification.key /home/secrets
COPY ./helm-drupal /home/helm-drupal
COPY ./azure /home/azure

EXPOSE 8888
EXPOSE 8000
# The application's jar file
ARG JAR_FILE=./EPRequest/target/EPRequest-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} EPRequest.jar

COPY ./EPRequest/config/${CLUSTER_ENV}.eerequest.properties /home/config/eprequest.properties

RUN ./home/secrets/init.sh

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n
# Run the jar file 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/EPRequest.jar"]








