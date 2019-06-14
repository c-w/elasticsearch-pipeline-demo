package org.elasticsearch.plugin.ingest.azurestorage;

import com.microsoft.azure.storage.StorageException;

import java.net.URISyntaxException;

interface Storage {
    byte[] downloadBlob(String containerName, String blobName) throws URISyntaxException, StorageException;
}
