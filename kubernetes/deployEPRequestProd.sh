#!/bin/bash
cd ..
sudo docker build -t tbsdevelopmentcr.azurecr.io/ubuntu:latest .
kubectl config set-context --current --namespace=default
kubectl delete deployment/eprequestform
sudo az acr login --name tbsdevelopmentcr
sudo docker push tbsdevelopmentcr.azurecr.io/ubuntu:latest
kubectl run eprequestform --image=tbsdevelopmentcr.azurecr.io/ubuntu:latest
kubectl expose deployment eprequestform --type=LoadBalancer --port=80 --target-port=8888 



