#!/bin/bash
cd ..
mvn package
cd .. 
docker build -f ./EPRequest/Dockerfile . -t tbsacr.azurecr.io/$1-eprequest:latest --build-arg CLUSTER_ENV=$1
docker run -p8888:8888 -p8000:8000 tbsacr.azurecr.io/$1-eprequest:latest


