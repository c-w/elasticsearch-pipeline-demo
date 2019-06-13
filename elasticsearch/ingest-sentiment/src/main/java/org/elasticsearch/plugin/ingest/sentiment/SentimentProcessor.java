package org.elasticsearch.plugin.ingest.sentiment;

import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.Processor;

import java.net.URL;
import java.util.Map;
import java.util.Optional;

import static java.lang.System.getenv;
import static org.elasticsearch.ingest.ConfigurationUtils.newConfigurationException;
import static org.elasticsearch.ingest.ConfigurationUtils.readIntProperty;
import static org.elasticsearch.ingest.ConfigurationUtils.readStringProperty;

public class SentimentProcessor extends AbstractProcessor {
    static final String TYPE = "sentiment";

    private final String textField;
    private final String languageField;
    private final String targetField;
    private final SentimentAnalysis textAnalyticsClient;

    SentimentProcessor(String tag, String textField, String languageField, String targetField, SentimentAnalysis textAnalyticsClient) {
        super(tag);
        this.textField = textField;
        this.languageField = languageField;
        this.targetField = targetField;
        this.textAnalyticsClient = textAnalyticsClient;
    }

    @Override
    public IngestDocument execute(IngestDocument ingestDocument) {
        String text = ingestDocument.getFieldValue(textField, String.class);
        String language = ingestDocument.getFieldValue(languageField, String.class);

        Optional<Double> sentiment = textAnalyticsClient.fetchSentiment(text, language);
        sentiment.ifPresent(score -> ingestDocument.setFieldValue(targetField, score));

        return ingestDocument;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public static final class Factory implements Processor.Factory {
        @Override
        public SentimentProcessor create(Map<String, Processor.Factory> factories, String tag, Map<String, Object> config)
            throws Exception {

            String textField = readStringProperty(TYPE, tag, config, "text_field");
            String languageField = readStringProperty(TYPE, tag, config, "language_field");
            String targetField = readStringProperty(TYPE, tag, config, "target_field", "sentiment");
            Integer timeoutSeconds = readIntProperty(TYPE, tag, config, "timeout_seconds", 5);

            String azureTextAnalyticsEndpoint = readEnv(tag, "AZURE_TEXT_ANALYTICS_ENDPOINT");
            String azureTextAnalyticsKey = readEnv(tag, "AZURE_TEXT_ANALYTICS_KEY");

            SentimentAnalysis azureTextAnalyticsClient = new AzureTextAnalyticsClient(
                new JsonHttpClient(timeoutSeconds),
                azureTextAnalyticsKey,
                new URL(azureTextAnalyticsEndpoint));

            return new SentimentProcessor(tag, textField, languageField, targetField, azureTextAnalyticsClient);
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
