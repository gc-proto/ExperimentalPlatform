#!/bin/bash
cd EPRequest
mvn package
cd ..
sudo docker build -t tbstestacr.azurecr.io/ubuntu:latest .
sudo docker run -p8888:8888 tbstestacr.azurecr.io/ubuntu:latest


