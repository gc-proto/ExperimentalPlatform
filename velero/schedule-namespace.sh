velero schedule create daily-$1 --schedule="0 1 * * *" --ttl 168h0m0s --include-namespaces $1
velero schedule get
