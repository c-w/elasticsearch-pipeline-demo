package org.elasticsearch.plugin.ingest.sentiment;

import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.ingest.Processor;
import org.elasticsearch.plugins.IngestPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class IngestSentimentPlugin extends Plugin implements IngestPlugin {
    @Override
    public List<Setting<?>> getSettings() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Processor.Factory> getProcessors(Processor.Parameters parameters) {
        return MapBuilder.<String, Processor.Factory>newMapBuilder()
            .put(SentimentProcessor.TYPE, new SentimentProcessor.Factory())
            .immutableMap();
    }

}
