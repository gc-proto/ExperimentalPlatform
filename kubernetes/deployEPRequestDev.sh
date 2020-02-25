#!/bin/bash
cd ..
sudo docker build -t tbsacr.azurecr.io/eerequest:latest . --build-arg CLUSTER_ENV=tbs-devel
kubectl config set-context --current --namespace=default
kubectl delete deployment/eerequestdev
sudo az acr login --name tbsacr
sudo docker push tbsacr.azurecr.io/eerequest:latest
kubectl run eerequestdev --image=tbsacr.azurecr.io/eerequest:latest
kubectl expose deployment/eerequestdev --type="ClusterIP" --port 8888




