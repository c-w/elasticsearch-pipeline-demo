package org.elasticsearch.plugin.ingest.textanalytics;

import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.test.ESTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static org.elasticsearch.ingest.RandomDocumentPicks.randomIngestDocument;
import static org.elasticsearch.plugin.ingest.textanalytics.TextAnalyticsProcessor.ALL_ANALYZERS;

public class TextAnalyticsProcessorTests extends ESTestCase {
    private static final InputFields INPUT_FIELDS = new InputFields("doc_text", "doc_lang");
    private static final String TARGET_FIELD = "output";

    @SuppressWarnings("unchecked")
    public void testThatProcessorAddsSentimentAndKeyPhrases() {
        Map<String, Object> document = new HashMap<>();
        document.put(INPUT_FIELDS.getTextField(), "What a great day today!");
        document.put(INPUT_FIELDS.getLanguageField(), "en");

        TextAnalyticsProcessor processor = new TextAnalyticsProcessor(randomAlphaOfLength(10), INPUT_FIELDS, TARGET_FIELD, ALL_ANALYZERS,
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

                @Override
                public List<String> fetchEntities(String text, String language) {
                    List<String> entities = new ArrayList<>();
                    entities.add("today");
                    return entities;
                }
            });

        IngestDocument ingestDocument = randomIngestDocument(random(), document);
        processor.execute(ingestDocument);

        assertTrue(ingestDocument.hasField("output.sentiment"));
        Double sentiment = ingestDocument.getFieldValue("output.sentiment", Double.class);
        assertEquals(0.76, sentiment, 0.001);

        assertTrue(ingestDocument.hasField("output.keyphrases"));
        List<String> keyPhrases = ingestDocument.getFieldValue("output.keyphrases", List.class);
        assertEquals(1, keyPhrases.size());
        assertEquals("great day", keyPhrases.get(0));

        assertTrue(ingestDocument.hasField("output.entities"));
        List<String> entities = ingestDocument.getFieldValue("output.entities", List.class);
        assertEquals(1, entities.size());
        assertEquals("today", entities.get(0));
    }

    public void testThatProcessorExcludesDisabledAnayzers() {
        Set<String> analyzers = new HashSet<>();
        analyzers.add("sentiment");
        analyzers.add("entities");

        Map<String, Object> document = new HashMap<>();
        document.put(INPUT_FIELDS.getTextField(), "What a great day!");
        document.put(INPUT_FIELDS.getLanguageField(), "en");

        TextAnalyticsProcessor processor = new TextAnalyticsProcessor(randomAlphaOfLength(10), INPUT_FIELDS, TARGET_FIELD, analyzers,
            new TextAnalytics() {
                @Override
                public Optional<Double> fetchSentiment(String text, String language) {
                    return Optional.empty();
                }

                @Override
                public List<String> fetchKeyPhrases(String text, String language) {
                    return emptyList();
                }

                @Override
                public List<String> fetchEntities(String text, String language) {
                    return emptyList();
                }
            });

        IngestDocument ingestDocument = randomIngestDocument(random(), document);
        processor.execute(ingestDocument);

        assertTrue(ingestDocument.hasField("output.sentiment"));
        assertNull(ingestDocument.getFieldValue("output.sentiment", Double.class));
        assertFalse(ingestDocument.hasField("output.keyphrases"));
        assertTrue(ingestDocument.hasField("output.entities"));
        assertEquals(0, ingestDocument.getFieldValue("output.entities", List.class).size());
    }
}
