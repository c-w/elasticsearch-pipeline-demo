## Development setup

To get started, copy `.env.template` to `.env` and set the required configuration values.

Then, run the following snippet:

```bash
docker-compose up --build
```

The command will:

- Start a single-node ElasticSearch deployment.
- Set up an index and processing pipeline.
- Ingest sample documents.

## Development links

- [ElasticSearch UI](http://localhost:1358/?appname=devindex&url=http://localhost:9200)
