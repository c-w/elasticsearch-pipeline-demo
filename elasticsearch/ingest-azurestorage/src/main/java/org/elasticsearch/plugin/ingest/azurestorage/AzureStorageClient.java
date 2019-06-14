package org.elasticsearch.plugin.ingest.azurestorage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.InvalidKeyException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

class AzureStorageClient implements Storage {
    private final CloudBlobClient client;

    private AzureStorageClient(CloudBlobClient client) {
        this.client = client;
    }

    AzureStorageClient(String connectionString) throws URISyntaxException, InvalidKeyException {
        this(CloudStorageAccount.parse(connectionString).createCloudBlobClient());
    }

    @Override
    public byte[] downloadBlob(String containerName, String blobName) throws URISyntaxException, StorageException {
        CloudBlobContainer container = client.getContainerReference(containerName);
        CloudBlockBlob blob = container.getBlockBlobReference(blobName);

        ByteArrayOutputStream blobStream = new ByteArrayOutputStream();

        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Void>) () ->
            {
                blob.download(blobStream);
                return null;
            });
        } catch (PrivilegedActionException e) {
            throw (StorageException) e.getCause();
        }

        return blobStream.toByteArray();
    }
}
