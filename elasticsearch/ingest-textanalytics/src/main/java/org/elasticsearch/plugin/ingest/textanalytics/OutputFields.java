package org.elasticsearch.plugin.ingest.textanalytics;

class OutputFields {
    private final String keyPhrasesField;
    private final String sentimentField;

    OutputFields(String keyPhrasesField, String sentimentField) {
        this.keyPhrasesField = keyPhrasesField;
        this.sentimentField = sentimentField;
    }

    String getKeyPhrasesField() {
        return keyPhrasesField;
    }

    String getSentimentField() {
        return sentimentField;
    }
}
