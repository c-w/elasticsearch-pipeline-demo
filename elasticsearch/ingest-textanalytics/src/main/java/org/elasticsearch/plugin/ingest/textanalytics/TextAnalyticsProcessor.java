package org.elasticsearch.plugin.ingest.textanalytics;

import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.Processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.elasticsearch.ingest.ConfigurationUtils.readStringProperty;

public class TextAnalyticsProcessor extends AbstractProcessor {
    static final String TYPE = "textanalytics";

    private final InputFields inputFields;
    private final String targetField;
    private final TextAnalytics textAnalytics;

    TextAnalyticsProcessor(String tag, InputFields inputFields, String targetField, TextAnalytics textAnalytics) {
        super(tag);
        this.inputFields = inputFields;
        this.targetField = targetField;
        this.textAnalytics = textAnalytics;
    }

    @Override
    public IngestDocument execute(IngestDocument ingestDocument) {
        String text = ingestDocument.getFieldValue(inputFields.getTextField(), String.class);
        String language = ingestDocument.getFieldValue(inputFields.getLanguageField(), String.class);

        Map<String, Object> output = new HashMap<>();

        Optional<Double> sentiment = textAnalytics.fetchSentiment(text, language);
        sentiment.ifPresent(score -> output.put("sentiment", score));

        List<String> keyPhrases = textAnalytics.fetchKeyPhrases(text, language);
        if (!keyPhrases.isEmpty()) {
            output.put("key_phrases", keyPhrases);
        }

        List<String> entities = textAnalytics.fetchEntities(text, language);
        if (!entities.isEmpty()) {
            output.put("entities", entities);
        }

        ingestDocument.setFieldValue(targetField, output);

        return ingestDocument;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public static final class Factory implements Processor.Factory {
        private final TextAnalytics textAnalytics;

        Factory(TextAnalytics textAnalytics) {
            this.textAnalytics = textAnalytics;
        }

        @Override
        public TextAnalyticsProcessor create(Map<String, Processor.Factory> factories, String tag, Map<String, Object> config) {
            InputFields inputFields = new InputFields(
                readStringProperty(TYPE, tag, config, "text_field", null),
                readStringProperty(TYPE, tag, config, "language_field", null));

            String targetField = readStringProperty(TYPE, tag, config, "target_field", "textanalytics");

            return new TextAnalyticsProcessor(tag, inputFields, targetField, textAnalytics);
        }
    }
}
