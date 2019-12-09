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
COPY init.sh /
RUN chmod +x /init.sh
RUN /init.sh
RUN az aks get-credentials --resource-group tbstest-aks --name tbstest-aks --admin --overwrite-existing
RUN helm init --service-account tiller
RUN rm helm.tar.gz




