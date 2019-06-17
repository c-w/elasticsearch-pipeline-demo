package org.elasticsearch.plugin.ingest.textanalytics.dto;

@SuppressWarnings("unused")
public class AzureTextAnalyticsSentiment {
    private String id;
    private Double score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
