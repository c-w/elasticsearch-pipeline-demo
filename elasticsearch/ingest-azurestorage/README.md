# Elasticsearch Azure Storage Ingest Processor

This processor ingests documents from [Azure Storage](https://azure.microsoft.com/en-us/services/storage/).

## Usage

```
PUT _ingest/pipeline/azurestorage-pipeline
{
  "description": "A pipeline to load documents from Azure Storage",
  "processors": [
    {
      "azurestorage" : {}
    }
  ]
}

PUT /my-index/my-type/1?pipeline=azurestorage-pipeline
{
  "container" : "my-container",
  "blob": "/path/to/my-blob.pdf"
}

GET /my-index/my-type/1
{
  "container" : "my-container",
  "blob": "/path/to/my-blob.txt",
  "base64": "SGVsbG8gd29ybGQhCg=="
}
```

## Configuration

### Pipeline parameters

| Parameter | Required | Default | Description |
| --------- | -------- | ------- | ----------- |
| container_field | no | container | The field from which to get the storage container |
| blob_field | no | blob | The field from which to get the storage blob path |
| target_field | no | base64 | The field that will hold the base64-encoded blob content |

### Node environment variables

| Environment variable | Required | Default | Description |
| -------------------- | -------- | ------- | ----------- |
| AZURE_STORAGE_CONNECTION_STRING | yes | - | Connection string for Azure Storage |

## Setup

In order to install this plugin, you need to create a zip distribution first by running

```bash
gradle assembleZip
```

This will produce a zip file in `build/distributions`.

After building the zip file, you can install it like this

```bash
bin/elasticsearch-plugin install file:///path/to/ingest-azurestorage/build/distribution/ingest-azurestorage-0.0.1-SNAPSHOT.zip
```
