package org.elasticsearch.plugin.ingest.textanalytics.dto;

import java.util.List;

@SuppressWarnings("unused")
public class AzureTextAnalyticsSentimentResponse {
    private List<AzureTextAnalyticsSentiment> documents;

    public List<AzureTextAnalyticsSentiment> getDocuments() {
        return documents;
    }

    public void setDocuments(List<AzureTextAnalyticsSentiment> documents) {
        this.documents = documents;
    }
}
