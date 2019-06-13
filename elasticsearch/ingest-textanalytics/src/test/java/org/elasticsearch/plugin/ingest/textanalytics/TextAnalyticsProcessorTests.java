package org.elasticsearch.plugin.ingest.textanalytics;

import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.test.ESTestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.elasticsearch.ingest.RandomDocumentPicks.randomIngestDocument;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class TextAnalyticsProcessorTests extends ESTestCase {
    public void testThatProcessorAddsSentiment() {
        Map<String, Object> document = new HashMap<>();
        document.put("doc_text", "What a great day!");
        document.put("doc_lang", "en");

        TextAnalyticsProcessor processor = new TextAnalyticsProcessor(randomAlphaOfLength(10), "doc_text", "doc_lang", "key_phrases",
            "sentiment",
            new TextAnalytics() {
            @Override
            public Optional<Double> fetchSentiment(String text, String language) {
                return Optional.of(0.76);
            }

            @Override
            public List<String> fetchKeyPhrases(String text, String language) {
                return emptyList();
            }
        });

        IngestDocument ingestDocument = randomIngestDocument(random(), document);
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data, hasKey("sentiment"));
        assertThat(data.get("sentiment"), is(0.76));
    }

    public void testThatProcessorDoesNotAddNullSentiment() {
        Map<String, Object> document = new HashMap<>();
        document.put("doc_text", "What a great day!");
        document.put("doc_lang", "en");

        TextAnalyticsProcessor processor = new TextAnalyticsProcessor(randomAlphaOfLength(10), "doc_text", "doc_lang", "key_phrases",
            "sentiment",
            new TextAnalytics() {
            @Override
            public Optional<Double> fetchSentiment(String text, String language) {
                return Optional.empty();
            }

            @Override
            public List<String> fetchKeyPhrases(String text, String language) {
                return emptyList();
            }
        });

        IngestDocument ingestDocument = randomIngestDocument(random(), document);
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data, not(hasKey("sentiment")));
    }
}
