#!/bin/bash

# RUN SCRIPT IN STRICT MODE
set -euo pipefail
IFS=$'\n\t'


# EXPORT ENVIRONMENT VARIABLES
# Necessary because of bug (?) where kompose does not use .env
# Additionally, using these variables in this script
set -o allexport; source .env; set +o allexport
BUILD_TAG=$(echo $BUILD_TAG | tr "/" "-")
export BUILD_TAG


delete_acr_images () {
    local status_code=$?
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
    exit $status_code
}


deploy_acr_auth () {
    # ACR AUTH
    oc apply -f acr-auth.yaml
    oc secrets link default acr-auth --for=pull
    oc secrets link builder acr-auth
}


# RUN UNIT TESTS
docker-compose build


# LOGIN TO CLUSTER
oc login "$OC_MASTER_SERVER_DNS" \
    --username="$OC_USERNAME" \
    --password="$OC_PASSWORD" \
    --insecure-skip-tls-verify=true
oc project default


# ALLOW CONTAINERS TO RUN AS ROOT
oc adm policy add-scc-to-user anyuid -z default


# CREATE AUTH SECRET
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


# CREATE NEW PROJECT AND DEPLOY TO CLUSTER
oc_project_name="ci-$BUILD_TAG"
if oc get project | grep "$oc_project_name " > /dev/null; then
    oc project default
    oc delete project "$oc_project_name"
fi
oc new-project "$oc_project_name"
deploy_acr_auth
kompose up --provider=openshift
trap delete_acr_images EXIT


# CREATE CLEANUP PROJECT AND DEPLOY TO CLUSTER
oc_cleanup_project_name=cleanup
if ! oc get project | grep "$oc_cleanup_project_name " > /dev/null; then
    cp $HOME/.kube/config cleanup/.kube/
    oc new-project "$oc_cleanup_project_name"
    deploy_acr_auth
    kompose up -f docker-compose-cleanup.yml --provider=openshift
    oc expose svc "$oc_cleanup_project_name"
    echo "webhook URL: http://$(oc get route "$oc_cleanup_project_name" -o jsonpath='{.spec.host}')"
    oc project "$oc_project_name"
fi


# RUN INTEGRATION TESTS
# Simulating checking the status code of the integration test container with sleep
oc project "$oc_project_name"
sleep 10
oc get all