package org.elasticsearch.plugin.ingest.azurestorage;

import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.test.ESTestCase;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.elasticsearch.ingest.RandomDocumentPicks.randomIngestDocument;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

public class AzureStorageProcessorTests extends ESTestCase {
    private static final InputFields INPUT_FIELDS = new InputFields("container", "blob");
    private static final OutputFields OUTPUT_FIELDS = new OutputFields("base64");

    public void testThatProcessorAddsSentiment() throws Exception {
        Map<String, Object> document = new HashMap<>();
        document.put("container", "mycontainer");
        document.put("blob", "myblob");

        AzureStorageProcessor processor = new AzureStorageProcessor(randomAlphaOfLength(10), INPUT_FIELDS, OUTPUT_FIELDS, (container,
            blob) -> "Hello world".getBytes(UTF_8));

        IngestDocument ingestDocument = randomIngestDocument(random(), document);
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data, hasKey("base64"));
        assertThat(data.get("base64"), is("SGVsbG8gd29ybGQ="));
    }
}
