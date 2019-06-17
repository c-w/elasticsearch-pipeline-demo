package org.elasticsearch.plugin.ingest.textanalytics.dto;

import java.util.List;

@SuppressWarnings("unused")
public class AzureTextAnalyticsRequest {
    private List<AzureTextAnalyticsDocument> documents;

    public List<AzureTextAnalyticsDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<AzureTextAnalyticsDocument> documents) {
        this.documents = documents;
    }
}
