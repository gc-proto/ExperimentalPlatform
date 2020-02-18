#!/bin/bash
. setVars.sh
az login \
    --service-principal \
    -u $(aksSpId) \
    -p $(aksSpSecret) \
    --tenant $(aksSpTenantId)
az aks get-credentials \
    -n $(aks) \
    -g $(rg)
for instanceName in ./instance/new/*; do
    echo $filename
done