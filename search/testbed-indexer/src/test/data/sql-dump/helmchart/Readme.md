# Overview

This Helm Chart deployment service can be access within K8S cluster
```
http://$ServiceName:$servicePort
```
The $ServiceName is the `tbs-testbed-indexer-sql` 
The $servicePort is specify in the file `helmchart/values.yaml`, service.port value is `3306`
Any value inside the `values.yaml` can be changed when run helm install by using flag `--set`

No other k8s services would be accessing this deployment.  
The deployment will run then stay running the MySQL server on service port within the cluster to be consume by `tbs-testbed-indexer` 

## Installation and Deployment
Helm chart is initially created using command 
```
helm create helmchart
```

### Create Helm Chart for Application

Run the sample chart using

```
helm create <project-name>
```

For this project `helmchart`

```
helm create helmchart
```

Open and edit file `./helmchart/values.yaml` with the following changes

```
replicaCount: 1 

# The tag is dynamic when using helm install --set image.tag=<tag>
image:
  repository: docker.norconex.com/mydockerimage
  tag: latest  
  pullPolicy: IfNotPresent

containerPort: 3306

nameOverride: ""
fullnameOverride: ""

# Using ClusterIP only available as backend service
service:
  type: ClusterIP
  port: 3306
  
# leave everything else in this values.yaml below as default
...
```

Open and edit file `deployment.yaml` go to under the name `http` line 28 and change 

```
...
containerPort: {{ .Values.containerPort }}
...
```

Helm deployment is launch to serve indexer service on `ClusterIP port 3306` towards pod with name `tbs-testbed-solr`
on port `8983`

Using the `helmchart` folder for this helm chart

```
helm install --name tbs-testbed-indexer-sql helmchart/ --set image.tag=<latest-tag-build-here>
```

Delete and purge of current deployment is done using commmand

```
helm delete --purge tbs-testbed-indexer-sql
```