# Elasticsearch TextAnalytics Ingest Processor

This processor analyzes documents via [Azure Text Analytics](https://azure.microsoft.com/en-us/services/cognitive-services/text-analytics/).

## Usage

```
PUT _ingest/pipeline/textanalytics-pipeline
{
  "description": "A pipeline to use Azure Text Analytics",
  "processors": [
    {
      "sentiment" : {
        "text_field" : "doc_text",
        "language_field" : "doc_lang"
      }
    }
  ]
}

PUT /my-index/my-type/1?pipeline=textanalytics-pipeline
{
  "doc_text" : "What a great day!",
  "doc_lang": "en"
}

GET /my-index/my-type/1
{
  "doc_text" : "What a great day!",
  "doc_lang" : "en",
  "key_phrases": ["great day"],
  "sentiment": 1
}

PUT /my-index/my-type/2?pipeline=textanalytics-pipeline
{
  "doc_text" : "That makes me sad :(",
  "doc_lang": "en"
}

GET /my-index/my-type/2
{
  "doc_text" : "That makes me sad :(",
  "doc_lang" : "en",
  "key_phrases": [],
  "sentiment": 0.05
}
```

## Configuration

### Pipeline parameters

| Parameter | Required | Default | Description |
| --------- | -------- | ------- | ----------- |
| text_field | yes | - | The field from which to get the document text |
| language_field | yes | - | The field from which to get the document language |
| sentiment_field | no | sentiment | The field that will hold the sentiment score |
| key_phrases_field | no | key_phrases | The field that will hold the key phrases |
| timeout_seconds | no | 5 | The timeout for text analytics requests |

### Node environment variables

| Environment variable | Required | Default | Description |
| -------------------- | -------- | ------- | ----------- |
| AZURE_TEXT_ANALYTICS_ENDPOINT | yes | - | Endpoint for the Azure text analytics service |
| AZURE_TEXT_ANALYTICS_KEY | yes | - | Access key for the Azure text analytics service |

## Setup

In order to install this plugin, you need to create a zip distribution first by running

```bash
gradle assembleZip
```

This will produce a zip file in `build/distributions`.

After building the zip file, you can install it like this

```bash
bin/elasticsearch-plugin install file:///path/to/ingest-textanalytics/build/distribution/ingest-textanalytics-0.0.1-SNAPSHOT.zip
```
