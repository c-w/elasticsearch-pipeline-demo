FROM ubuntu:bionic

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

RUN apt-get update && apt-get install -y curl

# INSTALL OC FROM OKD
RUN curl -sSL https://github.com/openshift/origin/releases/download/v3.11.0/openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit.tar.gz | tar -xzv && \
    mv openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit/kubectl /usr/local/bin && \
    mv openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit/oc /usr/local/bin && \
    rm -rf openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit

# INSTALL GO
RUN curl -fsSL https://dl.google.com/go/go1.12.7.linux-amd64.tar.gz | tar -v -C /usr/local -xz
ENV PATH=$PATH:/usr/local/go/bin

EXPOSE 8080
COPY . /server
WORKDIR /server
RUN go build
CMD ["./server"]
