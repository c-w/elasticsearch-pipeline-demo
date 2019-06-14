package org.elasticsearch.plugin.ingest.azurestorage;

import com.microsoft.azure.storage.StorageException;
import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.Processor;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Map;

import static org.elasticsearch.ingest.ConfigurationUtils.readStringProperty;

public class AzureStorageProcessor extends AbstractProcessor {
    static final String TYPE = "azurestorage";

    private final InputFields inputFields;
    private final String targetField;
    private final Storage storage;

    AzureStorageProcessor(String tag, InputFields inputFields, String targetField, Storage storage) {
        super(tag);
        this.inputFields = inputFields;
        this.targetField = targetField;
        this.storage = storage;
    }

    @Override
    public IngestDocument execute(IngestDocument ingestDocument) throws URISyntaxException, StorageException {
        String containerName = ingestDocument.getFieldValue(inputFields.getContainerField(), String.class);
        String blobName = ingestDocument.getFieldValue(inputFields.getBlobField(), String.class);

        byte[] blob = storage.downloadBlob(containerName, blobName);

        String blobContent = Base64.getEncoder().encodeToString(blob);
        ingestDocument.setFieldValue(targetField, blobContent);

        return ingestDocument;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public static final class Factory implements Processor.Factory {
        private final Storage storage;

        Factory(Storage storage) {
            this.storage = storage;
        }

        @Override
        public AzureStorageProcessor create(Map<String, Processor.Factory> factories, String tag, Map<String, Object> config) {

            InputFields inputFields = new InputFields(
                readStringProperty(TYPE, tag, config, "container_field", "container"),
                readStringProperty(TYPE, tag, config, "blob_field", "blob"));

            String targetField = readStringProperty(TYPE, tag, config, "target_field", "base64");

            return new AzureStorageProcessor(tag, inputFields, targetField, storage);
        }
    }
}
