package org.elasticsearch.plugin.ingest.textanalytics;

import java.util.List;
import java.util.Optional;

interface TextAnalytics {
    Optional<Double> fetchSentiment(String text, String language);
    List<String> fetchKeyPhrases(String text, String language);
}
