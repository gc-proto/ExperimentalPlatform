#!/bin/bash
cd ../../docker/site-optimization
docker-compose -f docker-compose-covid19inv.yml build
docker push ryanhyma/covid19inv_nginx:1.0.0
kubectl rollout restart deployment/covid19inv-nginx --namespace covid19inv
