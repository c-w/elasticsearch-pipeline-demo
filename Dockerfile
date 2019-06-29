FROM ubuntu:bionic

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# INSTALL DOCKER
RUN apt-get update && \
    apt-get install -y apt-transport-https ca-certificates curl software-properties-common uuid-runtime && \
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add - && \
    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable" && \
    apt-get update && \
    apt-get install -y docker-ce && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# INSTALL DOCKER COMPOSE
RUN curl -fsSL "https://github.com/docker/compose/releases/download/1.21.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
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
RUN curl -sL https://aka.ms/InstallAzureCLIDeb | bash && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY . /app
WORKDIR /app
CMD ["./ci.sh"]
