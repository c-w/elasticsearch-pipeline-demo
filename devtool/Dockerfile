ARG PYTHON_VERSION=3.7

FROM python:${PYTHON_VERSION} AS builder

COPY requirements-dev.txt /reqs/
RUN pip install -r /reqs/requirements-dev.txt

COPY requirements.txt /reqs/
RUN pip install -r /reqs/requirements.txt \
 && pip wheel -r /reqs/requirements.txt -w /deps

COPY . /app/

WORKDIR /app
RUN invoke lint

FROM python:${PYTHON_VERSION}-slim AS runtime

COPY --from=builder /deps /deps
RUN pip install --no-cache-dir /deps/*.whl

COPY --from=builder /app /app

WORKDIR /app
ENTRYPOINT ["invoke"]
