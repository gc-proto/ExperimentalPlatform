#!/bin/bash
cd ..
sudo docker build -t tbstestacr.azurecr.io/ubuntu:latest .
kubectl config set-context --current --namespace=default
kubectl delete deployment/eprequestform
sudo az acr login --name tbstestacr
sudo docker push tbstestacr.azurecr.io/ubuntu:latest
kubectl run eprequestform --image=tbstestacr.azurecr.io/ubuntu:latest
kubectl expose deployment eprequestform --type=LoadBalancer --port=80 --target-port=8888 --load-balancer-ip=40.85.245.73



