FROM ubuntu:bionic

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

RUN apt-get update && \
    apt-get install -y curl git-core

# INSTALL DOCKER
RUN curl -fsSL https://get.docker.com | bash

# INSTALL DOCKER COMPOSE
RUN curl -fsSL "https://github.com/docker/compose/releases/download/1.21.2/docker-compose-Linux-x86_64" -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

# INSTALL KOMPOSE
RUN curl -fsSL https://github.com/kubernetes/kompose/releases/download/v1.17.0/kompose-linux-amd64 -o /usr/local/bin/kompose && \
    chmod +x /usr/local/bin/kompose

# INSTALL OC FROM OKD
RUN curl -sSL https://github.com/openshift/origin/releases/download/v3.11.0/openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit.tar.gz | tar -xzv && \
    mv openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit/kubectl /usr/local/bin && \
    mv openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit/oc /usr/local/bin && \
    rm -rf openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit

# INSTALL AZURE CLI
RUN curl -sL https://aka.ms/InstallAzureCLIDeb | bash

COPY . /app
WORKDIR /app
CMD ["./ci.sh"]
