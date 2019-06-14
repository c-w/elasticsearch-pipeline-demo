package org.elasticsearch.plugin.ingest.textanalytics;

import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.Processor;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.System.getenv;
import static org.elasticsearch.ingest.ConfigurationUtils.newConfigurationException;
import static org.elasticsearch.ingest.ConfigurationUtils.readIntProperty;
import static org.elasticsearch.ingest.ConfigurationUtils.readStringProperty;

public class TextAnalyticsProcessor extends AbstractProcessor {
    static final String TYPE = "textanalytics";

    private final InputFields inputFields;
    private final OutputFields outputFields;
    private final TextAnalytics textAnalytics;

    TextAnalyticsProcessor(String tag, InputFields inputFields, OutputFields outputFields, TextAnalytics textAnalytics) {
        super(tag);
        this.inputFields = inputFields;
        this.outputFields = outputFields;
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

        ingestDocument.setFieldValue(outputFields.getTargetField(), output);

        return ingestDocument;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public static final class Factory implements Processor.Factory {
        @Override
        public TextAnalyticsProcessor create(Map<String, Processor.Factory> factories, String tag, Map<String, Object> config)
            throws Exception {

            JsonHttpClient httpClient = new JsonHttpClient(
                readIntProperty(TYPE, tag, config, "timeout_seconds", 5),
                readIntProperty(TYPE, tag, config, "retry_interval_seconds", 1));

            TextAnalytics azureTextAnalyticsClient = new AzureTextAnalyticsClient(
                httpClient,
                readEnv(tag, "AZURE_TEXT_ANALYTICS_KEY"),
                new URL(readEnv(tag, "AZURE_TEXT_ANALYTICS_ENDPOINT")));

            InputFields inputFields = new InputFields(
                readStringProperty(TYPE, tag, config, "text_field", null),
                readStringProperty(TYPE, tag, config, "language_field", null));

            OutputFields outputFields = new OutputFields(
                readStringProperty(TYPE, tag, config, "target_field", "textanalytics"));

            return new TextAnalyticsProcessor(tag, inputFields, outputFields, azureTextAnalyticsClient);
        }

        private static String readEnv(String tag, String key) {
            String value = getenv(key);

            if (value == null || value.isEmpty()) {
                throw newConfigurationException(TYPE, tag, key, "required environment variable is missing");
            }

            return value;
        }
    }
}
