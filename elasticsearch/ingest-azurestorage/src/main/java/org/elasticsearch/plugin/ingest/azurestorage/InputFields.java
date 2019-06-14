package org.elasticsearch.plugin.ingest.azurestorage;

class InputFields {
    private final String containerField;
    private final String blobField;

    InputFields(String containerField, String blobField) {
        this.containerField = containerField;
        this.blobField = blobField;
    }

    String getContainerField() {
        return containerField;
    }

    String getBlobField() {
        return blobField;
    }
}
