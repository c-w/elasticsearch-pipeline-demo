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


@task
def lint(context):
    files = " ".join(shlex.quote(path) for path in iglob("*.py"))

    context.run("black --check {}".format(files))


@task
def create_es_index(_, name, host=ES_HOST, port=ES_PORT):
    client = Elasticsearch([{"host": host, "port": port}])

    client.indices.create(index=name, ignore=400)


@task
def create_es_pipeline(_, path, name=None, host=ES_HOST, port=ES_PORT):
    path = Path(path)
    name = name or path.stem
    pipeline = json.loads(path.read_text(encoding="utf-8"))
    client = Elasticsearch([{"host": host, "port": port}])

    client.ingest.put_pipeline(name, pipeline)
