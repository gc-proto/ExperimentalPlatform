# Overview

This Helm Chart deployment service can be access within K8S cluster
```
http://$ServiceName:$servicePort
```
The $ServiceName is the `tbs-testbed-indexer` 
The $servicePort is specify in the file `helmchart/values.yaml`, service.port value is `8983`
Any value inside the `values.yaml` can be changed when run helm install by using flag `--set`

No other k8s services would be accessing this deployment.  
The deployment will run then stay off when it completed it's indexing from 
the dockerfile defined from ENTRYPOINT to run shell script specific to indexing
service at `http://dev-template-solr:8983` 

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
  repository: docker.YOUR_HOST.com/mydockerimage
  tag: latest  
  pullPolicy: IfNotPresent

containerPort: 8983

nameOverride: ""
fullnameOverride: ""

# Using ClusterIP only available as backend service
service:
  type: ClusterIP
  port: 8983
  
# leave everything else in this values.yaml below as default
...
```

Open and edit file `deployment.yaml` go to under the name `http` line 28 and change 

```
...
containerPort: {{ .Values.containerPort }}
...
```

Helm deployment is launch to index service towards pod with name `dev-template-solr`
on port `8983`

Using the `helmchart` folder for this helm chart

```
helm install dev-template-solr helmchart/ --set image.tag=<latest-tag-build-here>
```

Delete and purge of current deployment is done using commmand

```
helm delete --purge dev-template-solr
```