package org.elasticsearch.plugin.ingest.textanalytics.dto;

import java.util.List;

@SuppressWarnings("unused")
public class AzureTextAnalyticsKeyphrasesResponse {
    private List<AzureTextAnalyticsKeyphrases> documents;

    public List<AzureTextAnalyticsKeyphrases> getDocuments() {
        return documents;
    }

    public void setDocuments(List<AzureTextAnalyticsKeyphrases> documents) {
        this.documents = documents;
    }
}
