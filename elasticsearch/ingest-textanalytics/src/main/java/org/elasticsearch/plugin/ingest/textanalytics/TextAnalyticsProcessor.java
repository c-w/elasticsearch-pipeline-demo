package org.elasticsearch.plugin.ingest.textanalytics;

import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.Processor;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.System.getenv;
import static org.elasticsearch.ingest.ConfigurationUtils.newConfigurationException;
import static org.elasticsearch.ingest.ConfigurationUtils.readIntProperty;
import static org.elasticsearch.ingest.ConfigurationUtils.readStringProperty;

public class TextAnalyticsProcessor extends AbstractProcessor {
    static final String TYPE = "textanalytics";

    private final String textField;
    private final String languageField;
    private final String keyPhrasesField;
    private final String sentimentField;
    private final TextAnalytics textAnalyticsClient;

    TextAnalyticsProcessor(String tag, String textField, String languageField, String keyPhrasesField, String sentimentField,
                           TextAnalytics textAnalyticsClient) {
        super(tag);
        this.textField = textField;
        this.languageField = languageField;
        this.keyPhrasesField = keyPhrasesField;
        this.sentimentField = sentimentField;
        this.textAnalyticsClient = textAnalyticsClient;
    }

    @Override
    public IngestDocument execute(IngestDocument ingestDocument) {
        String text = ingestDocument.getFieldValue(textField, String.class);
        String language = ingestDocument.getFieldValue(languageField, String.class);

        Optional<Double> sentiment = textAnalyticsClient.fetchSentiment(text, language);
        sentiment.ifPresent(score -> ingestDocument.setFieldValue(sentimentField, score));

        List<String> keyPhrases = textAnalyticsClient.fetchKeyPhrases(text, language);
        if (!keyPhrases.isEmpty()) {
            ingestDocument.setFieldValue(keyPhrasesField, keyPhrases);
        }

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

            String textField = readStringProperty(TYPE, tag, config, "text_field");
            String languageField = readStringProperty(TYPE, tag, config, "language_field");
            String keyPhrasesField = readStringProperty(TYPE, tag, config, "key_phrases_field", "key_phrases");
            String sentimentField = readStringProperty(TYPE, tag, config, "sentiment_field", "sentiment");
            Integer timeoutSeconds = readIntProperty(TYPE, tag, config, "timeout_seconds", 5);

            String azureTextAnalyticsEndpoint = readEnv(tag, "AZURE_TEXT_ANALYTICS_ENDPOINT");
            String azureTextAnalyticsKey = readEnv(tag, "AZURE_TEXT_ANALYTICS_KEY");

            TextAnalytics azureTextAnalyticsClient = new AzureTextAnalyticsClient(
                new JsonHttpClient(timeoutSeconds),
                azureTextAnalyticsKey,
                new URL(azureTextAnalyticsEndpoint));

            return new TextAnalyticsProcessor(tag, textField, languageField, keyPhrasesField, sentimentField, azureTextAnalyticsClient);
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
