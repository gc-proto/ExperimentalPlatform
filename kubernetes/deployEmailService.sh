#!/bin/bash
cd ..
echo environment $1
docker build --build-arg CLUSTER_ENV=$1 -t tbsacr.azurecr.io/$1-eprequest:latest . 
kubectl config set-context --current --namespace=covid19inv
kubectl delete deployment/emailservice
az acr login --name tbsacr
docker push tbsacr.azurecr.io/$1-eprequest:latest
kubectl run emailservice --image=tbsacr.azurecr.io/$1-eprequest:latest
kubectl expose deployment emailservice --type=ClusterIP --port=80 --target-port=8888 