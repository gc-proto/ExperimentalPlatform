#!/bin/bash
kubectl config set-context --current --namespace cert-manager
kubectl apply -f cluster-issuer-prod.yaml
kubectl apply -f cluster-issuer-staging.yaml