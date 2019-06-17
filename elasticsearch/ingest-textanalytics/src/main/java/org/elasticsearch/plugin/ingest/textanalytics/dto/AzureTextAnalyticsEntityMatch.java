package org.elasticsearch.plugin.ingest.textanalytics.dto;

@SuppressWarnings("unused")
public class AzureTextAnalyticsEntityMatch {
    private Double wikipediaScore;
    private Double entityTypeScore;
    private String text;
    private Integer offset;
    private Integer length;

    public Double getWikipediaScore() {
        return wikipediaScore;
    }

    public void setWikipediaScore(Double wikipediaScore) {
        this.wikipediaScore = wikipediaScore;
    }

    public Double getEntityTypeScore() {
        return entityTypeScore;
    }

    public void setEntityTypeScore(Double entityTypeScore) {
        this.entityTypeScore = entityTypeScore;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
