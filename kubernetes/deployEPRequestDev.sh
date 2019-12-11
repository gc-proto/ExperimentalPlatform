#!/bin/bash
cd ..
sudo docker build -t tbstestacr.azurecr.io/ubuntu:latest .
kubectl config set-context --current --namespace=default
kubectl delete deployment/eprequestformdev
sudo az acr login --name tbstestacr
sudo docker push tbstestacr.azurecr.io/ubuntu:latest
kubectl run eprequestformdev --image=tbstestacr.azurecr.io/ubuntu:latest




