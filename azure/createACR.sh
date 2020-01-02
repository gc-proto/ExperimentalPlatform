#!/bin/bash
az group create --name tbs-acr-rg --location canadacentral
az acr create --resource-group tbs-acr-rg --name tbsacr --sku Basic
