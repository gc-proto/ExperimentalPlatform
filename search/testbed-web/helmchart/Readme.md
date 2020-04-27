# Overview

This Helm Chart deployment service can be access via 
```
http://$NODE-IP:$nodePort
```
The $NODE-IP is the local IP address from the EC2 node 
name `nodes.dev.k8s.YOUR_HOST-cloud.com` and has ip `172.20.33.35`

The $nodePort is specify in the file `helmchart/values.yaml`, value is `31234`

So, to access from another EC2 that is setup as Domain Controller would be in the
same VPC as the node 172.20.33.35, in this case VPC is `vpc-066252325082dfba6 (dev.k8s.YOUR_HOST-cloud.com)`


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

Open and edit file `./helmchart/value.yaml` with the following changes

```
replicaCount: 1 

# The tag is dynamic when using helm install --set image.tag=<tag>
image:
  repository: docker.YOUR_HOST.com/mydockerimage
  tag: latest  
  pullPolicy: IfNotPresent

containerPort: 8080

nameOverride: ""
fullnameOverride: ""

# Use NodePort to expose the port for the 
# EC2 domain controller to use for web proxy to local Node IP
service:
  type: NodePort 
  port: 3036
  nodePort: 31234
  
# leave everything else in this values.yaml below as default
...
```

Open and edit file `deployment.yaml` go to under the name `http` line 28 and change 

```
...
containerPort: {{ .Values.containerPort }}
...
```

Change `livenessProbe:` and `readinessProbe:` under `httpGet:`

```
...
path: /testbed 
...
```

Open and edit file `service.yaml` and edit the `nodePort`

```
...
nodePort: {{ .Values.service.nodePort }}
...
```

Helm deployment is launch for service and pod with name `tbs-testbed-web`
Using the `helmchart` folder for this helm chart

```
helm install --name tbs-testbed-web helmchart/ --set image.tag=<latest-tag-build-here>
```

Delete and purge of current deployment is done using commmand

```
helm delete --purge tbs-testbed-web
```