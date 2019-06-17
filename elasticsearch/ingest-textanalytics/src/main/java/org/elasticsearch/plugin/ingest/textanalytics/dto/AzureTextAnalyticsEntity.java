package org.elasticsearch.plugin.ingest.textanalytics.dto;

import java.util.List;

@SuppressWarnings("unused")
public class AzureTextAnalyticsEntity {
    private String name;
    private List<AzureTextAnalyticsEntityMatch> matches;
    private String wikipediaLanguage;
    private String wikipediaId;
    private String wikipediaUrl;
    private String bingId;
    private String type;
    private String subType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AzureTextAnalyticsEntityMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<AzureTextAnalyticsEntityMatch> matches) {
        this.matches = matches;
    }

    public String getWikipediaLanguage() {
        return wikipediaLanguage;
    }

    public void setWikipediaLanguage(String wikipediaLanguage) {
        this.wikipediaLanguage = wikipediaLanguage;
    }

    public String getWikipediaId() {
        return wikipediaId;
    }

    public void setWikipediaId(String wikipediaId) {
        this.wikipediaId = wikipediaId;
    }

    public String getWikipediaUrl() {
        return wikipediaUrl;
    }

    public void setWikipediaUrl(String wikipediaUrl) {
        this.wikipediaUrl = wikipediaUrl;
    }

    public String getBingId() {
        return bingId;
    }

    public void setBingId(String bingId) {
        this.bingId = bingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }
}
