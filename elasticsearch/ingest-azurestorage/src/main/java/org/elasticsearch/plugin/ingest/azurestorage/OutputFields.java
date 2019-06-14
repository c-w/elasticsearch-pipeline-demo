package org.elasticsearch.plugin.ingest.azurestorage;

class OutputFields {
    private final String base64Field;

    OutputFields(String base64Field) {
        this.base64Field = base64Field;
    }

    String getBase64Field() {
        return base64Field;
    }
}
