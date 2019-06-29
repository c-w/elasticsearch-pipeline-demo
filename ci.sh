#!/bin/bash

# RUN SCRIPT IN STRICT MODE
# Ensure no variable is unset
BUILD_ID=''
OC_PROJECT_NAME=''
IS_DEPLOYED_TO_CLUSTER=''
set -euo pipefail
IFS=$'\n\t'


# DELETE RESOURCES WHEN FINISHED
cleanup () {
    STATUS_CODE=$?
    if [ "$OC_PROJECT_NAME" ]; then
        oc project default
        oc delete project "$OC_PROJECT_NAME"
    fi
    if [ "$IS_DEPLOYED_TO_CLUSTER" ]; then
        # DELETE CI IMAGES
        az login --service-principal \
            --username "$SERVICE_PRINCIPAL_APP_ID" \
            --password "$SERVICE_PRINCIPAL_PASSWORD" \
            --tenant "$SERVICE_PRINCIPAL_TENANT_ID" \
            --output none
        IMAGES=$(docker-compose config | grep image: | sed s-"$CONTAINER_REGISTRY_URL/"--g | sed s-image:--g | sed s-' '--g)
        for IMAGE in $IMAGES; do
            REGISTRY_NAME=$(echo "$CONTAINER_REGISTRY_URL" | cut -f1 -d".")
            az acr repository delete --name "$REGISTRY_NAME" --image "$IMAGE" --yes
        done
    fi
    exit $STATUS_CODE
}
trap cleanup EXIT


# EXPORT ENVIRONMENT VARIABLES
# Necessary because of bug (?) where kompose does not use .env
# Additionally, using these variables in this script
set -o allexport; source .env; set +o allexport
if [ -z "$BUILD_ID" ]; then
    BUILD_ID=$(echo "$(git rev-parse --abbrev-ref HEAD)" | awk '{print tolower($0)}')
    export BUILD_ID
fi


# RUN UNIT TESTS
docker-compose build


# LOGIN TO CLUSTER
oc login "$OC_MASTER_SERVER_DNS" \
    --username="$OC_USERNAME" \
    --password="$OC_PASSWORD" \
    --insecure-skip-tls-verify=true


# CREATE NEW CLUSTER NAMESPACE
UUID=$(uuidgen)
OC_PROJECT_NAME="ci-$BUILD_ID-$UUID"
oc new-project "$OC_PROJECT_NAME"


# ALLOW CONTAINERS TO RUN AS ROOT
oc adm policy add-scc-to-user anyuid -z default


# ADD ACR AUTH SECRET TO CLUSTER'S DEFAULT USERS
# Logging in creates ~/.docker/config.json with auth credentials
docker login \
    --username "$SERVICE_PRINCIPAL_APP_ID" \
    --password "$SERVICE_PRINCIPAL_PASSWORD" \
    "$CONTAINER_REGISTRY_URL"
# Openshift base64 bug encoding workaround
DOCKER_CONFIG_B64=$(base64 -w0 ~/.docker/config.json)
oc create secret docker-registry acr-auth \
    --docker-server=unused \
    --docker-username=unused \
    --docker-password=unused \
    --docker-email=unused \
    --dry-run \
    -o yaml \
> acr-auth.yaml
sed -i "s/  .dockerconfigjson.*/  .dockerconfigjson: $DOCKER_CONFIG_B64/" acr-auth.yaml
oc apply -f acr-auth.yaml
rm acr-auth.yaml

oc secrets link default acr-auth --for=pull
oc secrets link builder acr-auth


# PUSH IMAGES TO ACR AND DEPLOY TO CLUSTER
kompose up --provider=openshift
IS_DEPLOYED_TO_CLUSTER=TRUE


# RUN INTEGRATION TESTS
# Simulating checking the status code of the integration test container with sleep
sleep 10
oc get all
