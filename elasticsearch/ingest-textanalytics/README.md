# Elasticsearch TextAnalytics Ingest Processor

This processor analyzes documents via [Azure Text Analytics](https://azure.microsoft.com/en-us/services/cognitive-services/text-textAnalytics/).

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
  "doc_text" : "What a great day today!",
  "doc_lang" : "en",
  "textanalytics": {
    "entities": ["today"],
    "key_phrases": ["great day"],
    "sentiment": 1
  }
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
  "textanalytics": {
    "sentiment": 0.05
  }
}
```

## Configuration

### Pipeline parameters

| Parameter | Required | Default | Description |
| --------- | -------- | ------- | ----------- |
| text_field | yes | - | The field from which to get the document text |
| language_field | yes | - | The field from which to get the document language |
| target_field | no | textanalytics | The field that will hold the text analytics |

### Elasticsearch settings

| Setting | Required | Default | Description |
| ------- | -------- | ------- | ----------- |
| ingest.textanalytics.endpoint | yes | - | Endpoint for the Azure text analytics service |
| ingest.textanalytics.key | yes | - | Access key for the Azure text analytics service |
| ingest.textanalytics.requesttimeout | no | 5 | Timeout (in seconds) for text analytics requests |
| ingest.textanalytics.retryinterval | no | 1 | Interval (in seconds) between retries of failed text analytics requests |

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
