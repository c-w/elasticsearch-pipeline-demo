package org.elasticsearch.plugin.ingest.textanalytics.dto;

import java.util.List;

@SuppressWarnings("unused")
public class AzureTextAnalyticsEntities {
    private String id;
    private List<AzureTextAnalyticsEntity> entities;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AzureTextAnalyticsEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<AzureTextAnalyticsEntity> entities) {
        this.entities = entities;
    }
}
