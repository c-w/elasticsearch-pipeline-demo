#!/bin/bash

# RUN SCRIPT IN STRICT MODE
set -euo pipefail
IFS=$'\n\t'


# LOGIN TO CLUSTER
oc login "$OC_MASTER_SERVER_DNS" \
    --username="$OC_USERNAME" \
    --password="$OC_PASSWORD" \
    --insecure-skip-tls-verify=true