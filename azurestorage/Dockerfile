FROM azuresdk/azure-cli-python:rc2.0.52

RUN adduser -Ds /bin/sh azuser

COPY --chown=azuser:azuser . /

USER azuser
CMD az storage container create -n "${AZURE_STORAGE_CONTAINER}" && az storage blob upload-batch -d "${AZURE_STORAGE_CONTAINER}" -s /examples
