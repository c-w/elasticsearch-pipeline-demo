#!/bin/bash

# RUN SCRIPT IN STRICT MODE
# Ensure no variable is unset
oc_project_name=''
is_deployed_to_cluster=''
set -euo pipefail
IFS=$'\n\t'


# DELETE RESOURCES WHEN FINISHED
cleanup () {
    local status_code=$?
    if [ "$oc_project_name" ]; then
        oc project default
        oc delete project "$oc_project_name"
    fi
    if [ "$is_deployed_to_cluster" ]; then
        # DELETE CI IMAGES
        az login --service-principal \
            --username "$SERVICE_PRINCIPAL_APP_ID" \
            --password "$SERVICE_PRINCIPAL_PASSWORD" \
            --tenant "$SERVICE_PRINCIPAL_TENANT_ID" \
            --output none
        local images
        local registry_name
        images=($(docker-compose config | grep image: | sed "s|$CONTAINER_REGISTRY_URL/||g" | sed "s|image:||g" | tr -d ' '))
        for image in ${images[@]}; do
            registry_name="${CONTAINER_REGISTRY_URL%%.*}"
            az acr repository delete --name "$registry_name" --image "$image" --yes
        done
    fi
    exit $status_code
}
trap cleanup EXIT


# EXPORT ENVIRONMENT VARIABLES
# Necessary because of bug (?) where kompose does not use .env
# Additionally, using these variables in this script
set -o allexport; source .env; set +o allexport
BUILD_TAG=$(echo $BUILD_TAG | tr "/" "-")
export BUILD_TAG


# RUN UNIT TESTS
docker-compose build


# LOGIN TO CLUSTER
oc login "$OC_MASTER_SERVER_DNS" \
    --username="$OC_USERNAME" \
    --password="$OC_PASSWORD" \
    --insecure-skip-tls-verify=true


# CREATE NEW CLUSTER NAMESPACE
oc_project_name="ci-$BUILD_TAG"
oc new-project "$oc_project_name"


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
is_deployed_to_cluster=TRUE


# RUN INTEGRATION TESTS
# Simulating checking the status code of the integration test container with sleep
sleep 10
oc get all
