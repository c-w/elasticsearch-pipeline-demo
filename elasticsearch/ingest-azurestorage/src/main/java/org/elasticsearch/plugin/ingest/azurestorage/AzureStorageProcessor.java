package org.elasticsearch.plugin.ingest.azurestorage;

import com.microsoft.azure.storage.StorageException;
import org.elasticsearch.ingest.AbstractProcessor;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.ingest.Processor;

import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Map;

import static java.lang.System.getenv;
import static org.elasticsearch.ingest.ConfigurationUtils.newConfigurationException;
import static org.elasticsearch.ingest.ConfigurationUtils.readStringProperty;

public class AzureStorageProcessor extends AbstractProcessor {
    static final String TYPE = "azurestorage";

    private final InputFields inputFields;
    private final OutputFields outputFields;
    private final Storage storage;

    AzureStorageProcessor(String tag, InputFields inputFields, OutputFields outputFields, Storage storage) {
        super(tag);
        this.inputFields = inputFields;
        this.outputFields = outputFields;
        this.storage = storage;
    }

    @Override
    public IngestDocument execute(IngestDocument ingestDocument) throws URISyntaxException, StorageException {
        String containerName = ingestDocument.getFieldValue(inputFields.getContainerField(), String.class);
        String blobName = ingestDocument.getFieldValue(inputFields.getBlobField(), String.class);

        byte[] blob = storage.downloadBlob(containerName, blobName);

        String blobContent = Base64.getEncoder().encodeToString(blob);
        ingestDocument.setFieldValue(outputFields.getBase64Field(), blobContent);

        return ingestDocument;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public static final class Factory implements Processor.Factory {
        @Override
        public AzureStorageProcessor create(Map<String, Processor.Factory> factories, String tag, Map<String, Object> config)
            throws Exception {

            InputFields inputFields = new InputFields(
                readStringProperty(TYPE, tag, config, "container_field", "container"),
                readStringProperty(TYPE, tag, config, "blob_field", "blob"));

            OutputFields outputFields = new OutputFields(
                readStringProperty(TYPE, tag, config, "target_field", "base64"));

            Storage azureStorage = new AzureStorageClient(
                readEnv(tag, "AZURE_STORAGE_CONNECTION_STRING"));

            return new AzureStorageProcessor(tag, inputFields, outputFields, azureStorage);
        }

        /** @noinspection SameParameterValue*/
        private static String readEnv(String tag, String key) {
            String value = getenv(key);

            if (value == null || value.isEmpty()) {
                throw newConfigurationException(TYPE, tag, key, "required environment variable is missing");
            }

            return value;
        }
    }
}
