#!/bin/bash
kubectl config set-context --current --namespace=default
sudo az acr login --name tbstestacr
sudo docker push tbstestacr.azurecr.io/ubuntu:v1
kubectl run eprequestform --image=tbstestacr.azurecr.io/ubuntu:v1
kubectl expose deployment eprequestform --type=LoadBalancer --port=80 --target-port=8888 --load-balancer-ip=13.88.248.50



