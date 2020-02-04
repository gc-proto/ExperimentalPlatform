#!/bin/bash
kubectl get namespace $1 -o json > $1.json
kubectl replace --raw "/api/v1/namespaces/$1/finalize" -f ./$1.json
rm $1.json