package org.elasticsearch.plugin.ingest.sentiment;

import java.util.Optional;

interface SentimentAnalysis {
    Optional<Double> fetchSentiment(String text, String language);
}
