package org.elasticsearch.plugin.ingest.textanalytics;

import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.ingest.Processor;
import org.elasticsearch.plugins.IngestPlugin;
import org.elasticsearch.plugins.Plugin;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.settings.Setting.Property.NodeScope;

@SuppressWarnings("unused")
public class IngestTextAnalyticsPlugin extends Plugin implements IngestPlugin {
    private static final String ENDPOINT_SETTING = "ingest.textanalytics.endpoint";
    private static final String KEY_SETTING = "ingest.textanalytics.key";
    private static final String REQUEST_TIMEOUT_SETTING = "ingest.textanalytics.requesttimeout";
    private static final String RETRY_INTERVAL_SETTING = "ingest.textanalytics.retryinterval";

    private static final int DEFAULT_REQUEST_TIMEOUT = 5;
    private static final int DEFAULT_RETRY_INTERVAL = 1;

    @Override
    public List<Setting<?>> getSettings() {
        List<Setting<?>> settings = new ArrayList<>();
        settings.add(Setting.simpleString(ENDPOINT_SETTING, NodeScope));
        settings.add(Setting.simpleString(KEY_SETTING, NodeScope));
        settings.add(Setting.intSetting(REQUEST_TIMEOUT_SETTING, DEFAULT_REQUEST_TIMEOUT, NodeScope));
        settings.add(Setting.intSetting(RETRY_INTERVAL_SETTING, DEFAULT_RETRY_INTERVAL, NodeScope));
        return settings;
    }

    @Override
    public Map<String, Processor.Factory> getProcessors(Processor.Parameters parameters) {
        Settings settings = parameters.env.settings();

        JsonHttpClient httpClient = new JsonHttpClient(
            settings.getAsInt(REQUEST_TIMEOUT_SETTING, DEFAULT_REQUEST_TIMEOUT),
            settings.getAsInt(RETRY_INTERVAL_SETTING, DEFAULT_RETRY_INTERVAL));

        TextAnalytics textAnalytics;
        try {
            textAnalytics = new AzureTextAnalyticsClient(httpClient,
                settings.get(KEY_SETTING),
                new URL(settings.get(ENDPOINT_SETTING)));
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return MapBuilder.<String, Processor.Factory>newMapBuilder()
            .put(TextAnalyticsProcessor.TYPE, new TextAnalyticsProcessor.Factory(textAnalytics))
            .immutableMap();
    }
}
