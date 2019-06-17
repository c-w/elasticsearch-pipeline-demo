package org.elasticsearch.plugin.ingest.textanalytics;

import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.Processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.elasticsearch.ingest.ConfigurationUtils.readOptionalList;
import static org.elasticsearch.ingest.ConfigurationUtils.readStringProperty;

public class TextAnalyticsProcessor extends AbstractProcessor {
    private static final String SENTIMENT_ANALYZER = "sentiment";
    private static final String KEYPHRASES_ANALYZER = "keyphrases";
    private static final String ENTITIES_ANALYZER = "entities";

    static final Set<String> ALL_ANALYZERS = new HashSet<>(asList(SENTIMENT_ANALYZER, KEYPHRASES_ANALYZER, ENTITIES_ANALYZER));
    static final String TYPE = "textanalytics";

    private final InputFields inputFields;
    private final String targetField;
    private final Set<String> analyzers;
    private final TextAnalytics textAnalytics;

    TextAnalyticsProcessor(String tag, InputFields inputFields, String targetField, Set<String> analyzers, TextAnalytics textAnalytics) {
        super(tag);
        this.inputFields = inputFields;
        this.targetField = targetField;
        this.analyzers = analyzers;
        this.textAnalytics = textAnalytics;
    }

    @Override
    public IngestDocument execute(IngestDocument ingestDocument) {
        String text = ingestDocument.getFieldValue(inputFields.getTextField(), String.class);
        String language = ingestDocument.getFieldValue(inputFields.getLanguageField(), String.class);

        Map<String, Object> output = new HashMap<>();

        if (analyzers.contains(SENTIMENT_ANALYZER)) {
            Optional<Double> sentiment = textAnalytics.fetchSentiment(text, language);
            output.put(SENTIMENT_ANALYZER, sentiment.orElse(null));
        }

        if (analyzers.contains(KEYPHRASES_ANALYZER)) {
            List<String> keyPhrases = textAnalytics.fetchKeyPhrases(text, language);
            output.put(KEYPHRASES_ANALYZER, keyPhrases);
        }

        if (analyzers.contains(ENTITIES_ANALYZER)) {
            List<String> entities = textAnalytics.fetchEntities(text, language);
            output.put(ENTITIES_ANALYZER, entities);
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

            List<String> enabledAnalyzers = readOptionalList(TYPE, tag, config, "analyzers");
            Set<String> analyzers = enabledAnalyzers == null || enabledAnalyzers.isEmpty()
                ? ALL_ANALYZERS
                : new HashSet<>(enabledAnalyzers);

            String targetField = readStringProperty(TYPE, tag, config, "target_field", "textanalytics");

            return new TextAnalyticsProcessor(tag, inputFields, targetField, analyzers, textAnalytics);
        }
    }
}
