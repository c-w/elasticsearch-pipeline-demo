FROM ubuntu:bionic AS builder

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

RUN apt-get update && apt-get install -y curl

# INSTALL OC FROM OKD
RUN curl -sSL https://github.com/openshift/origin/releases/download/v3.11.0/openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit.tar.gz | tar -xzv && \
    mv openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit/kubectl /usr/local/bin && \
    mv openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit/oc /usr/local/bin && \
    rm -rf openshift-origin-client-tools-v3.11.0-0cbc58b-linux-64bit

FROM python:3.7 AS runtime

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

COPY --from=builder /usr/local/bin/kubectl /usr/local/bin/kubectl
COPY --from=builder /usr/local/bin/oc /usr/local/bin/oc

EXPOSE 8000
COPY . /cleanup
WORKDIR /cleanup
RUN pip3 install -r requirements.txt && \
    chmod +x login.sh
CMD ["/bin/bash", "-c", "./login.sh && hug -f server.py"]