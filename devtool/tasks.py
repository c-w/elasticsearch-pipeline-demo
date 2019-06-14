from glob import iglob
from os.path import expandvars
from pathlib import Path
from time import sleep
import json
import shlex

from elasticsearch import Elasticsearch
from elasticsearch import ElasticsearchException
from environs import Env
from invoke import task

_ENV = Env()
_ENV.read_env()

ES_HOST = _ENV("ES_HOST", "localhost")
ES_PORT = _ENV.int("ES_PORT", 9200)
ES_PIPELINE = _ENV("ES_PIPELINE", "")
ES_INDEX = _ENV("ES_INDEX", "")
ES_TIMEOUT = _ENV.int("ES_TIMEOUT_SECONDS", 120)


@task
def lint(context):
    files = " ".join(shlex.quote(path) for path in iglob("*.py"))

    context.run("black --check {}".format(files))


def wait_for_elasticsearch(host, port):
    client = Elasticsearch([{"host": host, "port": port}], timeout=ES_TIMEOUT)

    connected = False
    while not connected:
        try:
            health = client.cluster.health()
        except ElasticsearchException:
            pass
        else:
            connected = health["status"] in {"yellow", "green"}

        if not connected:
            sleep(1)

    return client


@task
def create_es_index(_, name=ES_INDEX, host=ES_HOST, port=ES_PORT):
    search_client = wait_for_elasticsearch(host, port)

    search_client.indices.create(index=name, ignore=400)


@task
def create_es_pipeline(_, path, name=ES_PIPELINE, host=ES_HOST, port=ES_PORT):
    path = Path(path)
    search_client = wait_for_elasticsearch(host, port)

    pipeline = json.loads(expandvars(path.read_text(encoding="utf-8")))

    search_client.ingest.put_pipeline(name, pipeline)


@task
def create_es_document(
    _, blob, container, index=ES_INDEX, pipeline=ES_PIPELINE, host=ES_HOST, port=ES_PORT
):
    search_client = wait_for_elasticsearch(host, port)

    body = {"azurestorage": {"container": container, "blob": blob}}

    search_client.index(index=index, pipeline=pipeline, body=body)
