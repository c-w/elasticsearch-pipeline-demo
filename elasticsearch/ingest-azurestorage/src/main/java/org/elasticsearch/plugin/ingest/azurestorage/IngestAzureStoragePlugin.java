package org.elasticsearch.plugin.ingest.azurestorage;

import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.ingest.Processor;
import org.elasticsearch.plugins.IngestPlugin;
import org.elasticsearch.plugins.Plugin;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.settings.Setting.Property.NodeScope;

@SuppressWarnings("unused")
public class IngestAzureStoragePlugin extends Plugin implements IngestPlugin {
    private static final String CONNECTION_STRING_SETTING = "ingest.azurestorage.connectionstring";

    @Override
    public List<Setting<?>> getSettings() {
        List<Setting<?>> settings = new ArrayList<>();
        settings.add(Setting.simpleString(CONNECTION_STRING_SETTING, NodeScope));
        return settings;
    }

    @Override
    public Map<String, Processor.Factory> getProcessors(Processor.Parameters parameters) {
        Settings settings = parameters.env.settings();

        Storage azureStorage;
        try {
            azureStorage = new AzureStorageClient(settings.get(CONNECTION_STRING_SETTING));
        } catch (URISyntaxException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return MapBuilder.<String, Processor.Factory>newMapBuilder()
            .put(AzureStorageProcessor.TYPE, new AzureStorageProcessor.Factory(azureStorage))
            .immutableMap();
    }
}
