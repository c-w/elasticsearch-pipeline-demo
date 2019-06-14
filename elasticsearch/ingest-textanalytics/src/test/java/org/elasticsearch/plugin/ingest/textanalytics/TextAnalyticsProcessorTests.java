package org.elasticsearch.plugin.ingest.textanalytics;

import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.test.ESTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.elasticsearch.ingest.RandomDocumentPicks.randomIngestDocument;

public class TextAnalyticsProcessorTests extends ESTestCase {
    private static final InputFields INPUT_FIELDS = new InputFields("doc_text", "doc_lang");
    private static final OutputFields OUTPUT_FIELDS = new OutputFields("output");

    @SuppressWarnings("unchecked")
    public void testThatProcessorAddsSentimentAndKeyPhrases() {
        Map<String, Object> document = new HashMap<>();
        document.put("doc_text", "What a great day!");
        document.put("doc_lang", "en");

        TextAnalyticsProcessor processor = new TextAnalyticsProcessor(randomAlphaOfLength(10), INPUT_FIELDS, OUTPUT_FIELDS,
            new TextAnalytics() {
                @Override
                public Optional<Double> fetchSentiment(String text, String language) {
                    return Optional.of(0.76);
                }

                @Override
                public List<String> fetchKeyPhrases(String text, String language) {
                    List<String> keyPhrases = new ArrayList<>();
                    keyPhrases.add("great day");
                    return keyPhrases;
                }
            });

        IngestDocument ingestDocument = randomIngestDocument(random(), document);
        processor.execute(ingestDocument);

        assertTrue(ingestDocument.hasField("output.sentiment"));
        Double sentiment = ingestDocument.getFieldValue("output.sentiment", Double.class);
        assertEquals(0.76, sentiment, 0.001);

        assertTrue(ingestDocument.hasField("output.key_phrases"));
        List<String> keyPhrases = ingestDocument.getFieldValue("output.key_phrases", List.class);
        assertEquals(1, keyPhrases.size());
        assertEquals("great day", keyPhrases.get(0));
    }

    public void testThatProcessorDoesNotAddNullSentimentOrEmptyKeyPhrases() {
        Map<String, Object> document = new HashMap<>();
        document.put("doc_text", "What a great day!");
        document.put("doc_lang", "en");

        TextAnalyticsProcessor processor = new TextAnalyticsProcessor(randomAlphaOfLength(10), INPUT_FIELDS, OUTPUT_FIELDS,
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
        processor.execute(ingestDocument);

        assertFalse(ingestDocument.hasField("output.sentiment"));
        assertFalse(ingestDocument.hasField("output.key_phrases"));
    }
}
