#!/bin/bash
cd ..
mvn package
docker build . -f ./scripts/Dockerfile -t tbsacr.azurecr.io/pagesuccess-cj:1.0.0 
docker push tbsacr.azurecr.io/pagesuccess-cj:1.0.0