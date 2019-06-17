package org.elasticsearch.plugin.ingest.textanalytics.dto;

import java.util.List;

@SuppressWarnings("unused")
public class AzureTextAnalyticsEntitiesResponse {
    private List<AzureTextAnalyticsEntities> documents;

    public void setDocuments(List<AzureTextAnalyticsEntities> documents) {
        this.documents = documents;
    }

    public List<AzureTextAnalyticsEntities> getDocuments() {
        return documents;
    }
}
