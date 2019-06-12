# Elasticsearch Sentiment Ingest Processor

This processor analyzes document sentiment via [Azure Text Analytics](https://azure.microsoft.com/en-us/services/cognitive-services/text-analytics/).

## Usage

```
PUT _ingest/pipeline/sentiment-pipeline
{
  "description": "A pipeline to analyze sentiment",
  "processors": [
    {
      "sentiment" : {
        "azure_text_analytics_endpoint" : "https://$LOCATION.api.cognitive.microsoft.com",
        "azure_text_analytics_key" : "$ACCESS_KEY",
        "text_field" : "doc_text",
        "language_field" : "doc_lang"
      }
    }
  ]
}

PUT /my-index/my-type/1?pipeline=sentiment-pipeline
{
  "doc_text" : "What a great day!",
  "doc_lang": "en"
}

GET /my-index/my-type/1
{
  "doc_text" : "What a great day!",
  "doc_lang" : "en",
  "sentiment": 1
}

PUT /my-index/my-type/2?pipeline=sentiment-pipeline
{
  "doc_text" : "That makes me sad :(",
  "doc_lang": "en"
}

GET /my-index/my-type/2
{
  "doc_text" : "That makes me sad :(",
  "doc_lang" : "en",
  "sentiment": 0.05
}
```

## Configuration

| Parameter | Required | Default | Description |
| --------- | -------- | ------- | ----------- |
| text_field | yes | - | The field from which to get the document text |
| language_field | yes | - | The field from which to get the document language |
| azure_text_analytics_endpoint | yes | - | Endpoint for the Azure text analytics service |
| azure_text_analytics_key | yes | - | Access key for the Azure text analytics service |
| target_field | no | sentiment | The field that will hold the sentiment score |
| timeout_seconds | no | 5 | The timeout for text analytics requests |

## Setup

In order to install this plugin, you need to create a zip distribution first by running

```bash
gradle assembleZip
```

This will produce a zip file in `build/distributions`.

After building the zip file, you can install it like this

```bash
bin/elasticsearch-plugin install file:///path/to/ingest-sentiment/build/distribution/ingest-sentiment-0.0.1-SNAPSHOT.zip
```
