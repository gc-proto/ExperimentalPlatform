#!/bin/bash
ip=$(kubectl get svc --namespace $1 $1-drupal-nginx -o jsonpath="{.status.loadBalancer.ingress[*].ip}")
echo $ip
helm delete $1 --purge
kubectl delete namespace/$1
az network dns record-set a remove-record --resource-group dnszone --zone-name ryanhyma.com --record-set-name "$1" --ipv4-address $ip