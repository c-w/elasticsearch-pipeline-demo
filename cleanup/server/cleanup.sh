#!/bin/bash

# RUN SCRIPT IN STRICT MODE
set -euo pipefail
IFS=$'\n\t'


# EXPORT ENVIRONMENT VARIABLES
set -o allexport; source .env; set +o allexport


# LOGIN TO CLUSTER
oc login "$OC_MASTER_SERVER_DNS" \
    --username="$OC_USERNAME" \
    --password="$OC_PASSWORD" \
    --insecure-skip-tls-verify=true


oc project default
oc_project_name=$(cat project_to_delete.txt)
oc delete project "$oc_project_name"
