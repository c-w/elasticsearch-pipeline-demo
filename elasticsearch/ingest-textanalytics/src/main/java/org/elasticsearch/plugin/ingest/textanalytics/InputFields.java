package org.elasticsearch.plugin.ingest.textanalytics;

class InputFields {
    private final String textField;
    private final String languageField;

    InputFields(String textField, String languageField) {
        this.textField = textField;
        this.languageField = languageField;
    }

    String getTextField() {
        return textField;
    }

    String getLanguageField() {
        return languageField;
    }
}
