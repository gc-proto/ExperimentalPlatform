#!/bin/bash
cd ..
echo environment $1
sudo docker build --build-arg CLUSTER_ENV=$1 -t tbsacr.azurecr.io/$1:latest . 
kubectl config set-context --current --namespace=default
kubectl delete deployment/eprequestform
sudo az acr login --name tbsacr
sudo docker push tbsacr.azurecr.io/$1:latest
kubectl run eprequestform --image=tbsacr.azurecr.io/$1:latest
kubectl expose deployment eprequestform --type=LoadBalancer --port=80 --target-port=8888 



