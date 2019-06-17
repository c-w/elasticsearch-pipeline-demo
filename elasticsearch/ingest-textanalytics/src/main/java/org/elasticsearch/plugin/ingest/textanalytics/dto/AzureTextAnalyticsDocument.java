package org.elasticsearch.plugin.ingest.textanalytics.dto;

@SuppressWarnings("unused")
public class AzureTextAnalyticsDocument {
    private String id;
    private String language;
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
