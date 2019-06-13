from base64 import b64encode
from glob import iglob
from os.path import expandvars
from pathlib import Path
import json
import shlex

from azure.storage.blob import BlockBlobService
from elasticsearch import Elasticsearch
from environs import Env
from invoke import task

_ENV = Env()
_ENV.read_env()

ES_HOST = _ENV("ES_HOST", "localhost")
ES_PORT = _ENV.int("ES_PORT", 9200)
ES_PIPELINE = _ENV("ES_PIPELINE", "ingestion")
ES_INDEX = _ENV("ES_INDEX", "devindex")
STORAGE_CONNECTION_STRING = _ENV("AZURE_STORAGE_CONNECTION_STRING", "")


@task
def lint(context):
    files = " ".join(shlex.quote(path) for path in iglob("*.py"))

    context.run("black --check {}".format(files))


@task
def create_es_index(_, name=ES_INDEX, host=ES_HOST, port=ES_PORT):
    client = Elasticsearch([{"host": host, "port": port}])

    client.indices.create(index=name, ignore=400)


@task
def create_es_pipeline(_, path, name=ES_PIPELINE, host=ES_HOST, port=ES_PORT):
    path = Path(path)
    client = Elasticsearch([{"host": host, "port": port}])

    pipeline = json.loads(expandvars(path.read_text(encoding="utf-8")))

    client.ingest.put_pipeline(name, pipeline)


@task
def create_es_document(
    _,
    container,
    blob,
    index=ES_INDEX,
    pipeline=ES_PIPELINE,
    host=ES_HOST,
    port=ES_PORT,
    storage_connection_string=STORAGE_CONNECTION_STRING,
):
    storage_client = BlockBlobService(connection_string=storage_connection_string)
    blob = storage_client.get_blob_to_bytes(container, blob)

    client = Elasticsearch([{"host": host, "port": port}])

    body = {
        "raw": {
            "doc": b64encode(blob.content).decode("ascii"),
            "source": storage_client.make_blob_url(container, blob.name),
        }
    }

    client.index(index=index, pipeline=pipeline, body=body)
