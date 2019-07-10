#!/bin/bash

# RUN SCRIPT IN STRICT MODE
# Ensure no variable is unset
set -eEuo pipefail
IFS=$'\n\t'

# EXPORT ENVIRONMENT VARIABLES
# Necessary because of bug (?) where kompose does not use .env
# Additionally, using these variables in this script
set -o allexport; source .env; set +o allexport

# LOGIN TO CLUSTER
oc login "$OC_MASTER_SERVER_DNS" \
    --username="$OC_USERNAME" \
    --password="$OC_PASSWORD" \
    --insecure-skip-tls-verify=true

# CREATE NEW PROJECT
oc new-project cleanup


# ALLOW CONTAINERS TO RUN AS ROOT
oc adm policy add-scc-to-user anyuid -z default


# ADD ACR AUTH SECRET TO CLUSTER'S DEFAULT USERS
# Logging in creates ~/.docker/config.json with auth credentials
docker login \
    --username "$SERVICE_PRINCIPAL_APP_ID" \
    --password "$SERVICE_PRINCIPAL_PASSWORD" \
    "$CONTAINER_REGISTRY_URL"
# Openshift base64 bug encoding workaround
docker_config_b64=$(base64 -w0 ~/.docker/config.json)
oc create secret docker-registry acr-auth \
    --docker-server=unused \
    --docker-username=unused \
    --docker-password=unused \
    --docker-email=unused \
    --dry-run \
    -o yaml \
> acr-auth.yaml
sed -i "s/  .dockerconfigjson.*/  .dockerconfigjson: $docker_config_b64/" acr-auth.yaml
oc apply -f acr-auth.yaml
rm acr-auth.yaml

oc secrets link default acr-auth --for=pull
oc secrets link builder acr-auth


# PUSH IMAGES TO ACR AND DEPLOY TO CLUSTER
kompose up --provider=openshift
oc expose svc cleanup
echo "webhook URL: http://$(oc get route cleanup -o jsonpath='{.spec.host}')"
