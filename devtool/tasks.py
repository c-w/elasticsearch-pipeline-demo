from base64 import b64encode
from glob import iglob
from pathlib import Path
import json
import shlex

from elasticsearch import Elasticsearch
from environs import Env
from invoke import task

_ENV = Env()

ES_HOST = _ENV("ES_HOST", "localhost")
ES_PORT = _ENV.int("ES_PORT", 9200)
ES_PIPELINE = _ENV("ES_PIPELINE", "ingestion")
ES_INDEX = _ENV("ES_INDEX", "devindex")


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

    pipeline = json.loads(path.read_text(encoding="utf-8"))

    client.ingest.put_pipeline(name, pipeline)


@task
def create_es_document(
    _, path, index=ES_INDEX, pipeline=ES_PIPELINE, host=ES_HOST, port=ES_PORT
):
    path = Path(path)
    client = Elasticsearch([{"host": host, "port": port}])

    body = {
        "raw": {
            "doc": b64encode(path.read_bytes()).decode("ascii"),
            "source": str(path.resolve()),
        }
    }

    client.index(index=index, pipeline=pipeline, body=body)
