#!/bin/bash
ACR_ID=$(az acr show -n tbsacr -g tbs-acr-rg --query id -o tsv)
registryPassword=$(az ad sp create-for-rbac -n tbsacr-push --scopes $ACR_ID --role acrpush --query password -o tsv)
echo $registryPassword
AKS_ID=$(az aks show -n $1-aks -g $1-rg --query id -o tsv)
aksSpPassword=$(az ad sp create-for-rbac -n $1-aks-deploy --scopes $AKS_ID --role "Azure Kubernetes Service Cluster User Role" --query password -o tsv)
echo $aksSpPassword

