package org.elasticsearch.plugin.ingest.textanalytics;

class OutputFields {
    private final String targetField;

    OutputFields(String targetField) {
        this.targetField = targetField;
    }

    String getTargetField() {
        return targetField;
    }
}
