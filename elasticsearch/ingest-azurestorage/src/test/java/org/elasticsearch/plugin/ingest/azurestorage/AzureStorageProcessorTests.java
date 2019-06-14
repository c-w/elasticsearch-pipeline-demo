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
    private static final String TARGET_FIELD = "base64";

    public void testThatProcessorDownloadsBlob() throws Exception {
        Map<String, Object> document = new HashMap<>();
        document.put(INPUT_FIELDS.getContainerField(), "mycontainer");
        document.put(INPUT_FIELDS.getBlobField(), "myblob");

        AzureStorageProcessor processor = new AzureStorageProcessor(randomAlphaOfLength(10), INPUT_FIELDS, TARGET_FIELD,
            (container, blob) -> "Hello world".getBytes(UTF_8));

        IngestDocument ingestDocument = randomIngestDocument(random(), document);
        Map<String, Object> data = processor.execute(ingestDocument).getSourceAndMetadata();

        assertThat(data, hasKey(TARGET_FIELD));
        assertThat(data.get(TARGET_FIELD), is("SGVsbG8gd29ybGQ="));
    }
}
