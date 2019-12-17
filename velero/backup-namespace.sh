#!/bin/bash
velero backup create $1 --include-namespaces $2
