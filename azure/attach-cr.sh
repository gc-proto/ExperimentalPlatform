#!/bin/bash
az aks update -n $1-aks -g $1-rg --attach-acr tbsacr
