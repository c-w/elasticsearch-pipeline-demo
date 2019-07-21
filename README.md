[![Build Status](https://clewolff.visualstudio.com/elasticsearch-pipeline-demo/_apis/build/status/c-w.elasticsearch-pipeline-demo?branchName=master)](https://clewolff.visualstudio.com/elasticsearch-pipeline-demo/_build/latest?definitionId=6&branchName=master)

## What's this?

This repository is a demo project that implements a subset of the functionality of [Azure Cognitive Search](https://docs.microsoft.com/en-us/azure/search/cognitive-search-concept-intro) using Docker containers and ElasticSearch.

![Overview of architecture](https://user-images.githubusercontent.com/1086421/61595594-f4c5f880-abc6-11e9-8958-2fbfc535f0af.png)

## Development setup

To get started, copy `.env.template` to `.env` and set the required configuration values.

Next, build the project, run tests and linters:

```bash
docker-compose build
```

Then, execute the following snippet to run the project:

```bash
docker-compose up
```

The command will:

- Start a single-node ElasticSearch deployment.
- Set up an index and processing pipeline.
- Ingest sample documents.
- Convert formats such as DOCX or PDF to plain text.
- Analyze entities, key phrases and sentiment.

After the devtool scripts have completed, you can view the ingested/augmented documents in the [ElasticSearch management UI](http://localhost:1358/?appname=devindex&url=http://localhost:9200).
