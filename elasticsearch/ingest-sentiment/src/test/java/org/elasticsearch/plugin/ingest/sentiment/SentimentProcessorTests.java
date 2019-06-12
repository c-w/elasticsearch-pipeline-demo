package org.elasticsearch.plugin.ingest.sentiment;

import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.test.ESTestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.elasticsearch.ingest.RandomDocumentPicks.randomIngestDocument;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class SentimentProcessorTests extends ESTestCase {
    public void testThatProcessorAddsSentiment() {
        Map<String, Object> document = new HashMap<>();
        document.put("doc_text", "What a great day!");
        document.put("doc_lang", "en");

        SentimentProcessor processor = new SentimentProcessor(randomAlphaOfLength(10), "doc_text", "doc_lang", "sentiment",
            (text, language) -> Optional.of(0.76));

        IngestDocument ingestDocument = randomIngestDocument(random(), document);
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data, hasKey("sentiment"));
        assertThat(data.get("sentiment"), is(0.76));
    }

    public void testThatProcessorDoesNotAddNullSentiment() {
        Map<String, Object> document = new HashMap<>();
        document.put("doc_text", "What a great day!");
        document.put("doc_lang", "en");

        SentimentProcessor processor = new SentimentProcessor(randomAlphaOfLength(10), "doc_text", "doc_lang", "sentiment",
            (text, language) -> Optional.empty());

        IngestDocument ingestDocument = randomIngestDocument(random(), document);
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data, not(hasKey("sentiment")));
    }
}
