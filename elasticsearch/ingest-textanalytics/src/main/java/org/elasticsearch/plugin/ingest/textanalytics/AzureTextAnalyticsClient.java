package org.elasticsearch.plugin.ingest.textanalytics;

import org.apache.http.conn.ConnectTimeoutException;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsDocument;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsEntitiesResponse;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsEntity;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsKeyphrasesResponse;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsRequest;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsSentimentResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

class AzureTextAnalyticsClient implements TextAnalytics {
    private final JsonHttpClient httpClient;
    private final Map<String, String> authHeaders;
    private final URI keyPhrasesEndpoint;
    private final URI sentimentEndpoint;
    private final URI entitiesEndpoint;

    AzureTextAnalyticsClient(JsonHttpClient httpClient, String accessKey, URL endpoint) throws MalformedURLException, URISyntaxException {
        this.httpClient = httpClient;

        this.authHeaders = new HashMap<>();
        this.authHeaders.put("Ocp-Apim-Subscription-Key", accessKey);

        this.keyPhrasesEndpoint = new URL(endpoint, "/text/analytics/v2.1/keyPhrases").toURI();
        this.sentimentEndpoint = new URL(endpoint, "/text/analytics/v2.1/sentiment").toURI();
        this.entitiesEndpoint = new URL(endpoint, "/text/analytics/v2.1/entities").toURI();
    }

    @Override
    public Optional<Double> fetchSentiment(String text, String language) {
        AzureTextAnalyticsRequest request = buildTextAnalyticsRequest(text, language);

        AzureTextAnalyticsSentimentResponse response;
        try {
            response = httpClient.post(sentimentEndpoint, request, authHeaders, AzureTextAnalyticsSentimentResponse.class);
        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(response
            .getDocuments()
            .get(0)
            .getScore());
    }

    @Override
    public List<String> fetchKeyPhrases(String text, String language) {
        AzureTextAnalyticsRequest request = buildTextAnalyticsRequest(text, language);

        AzureTextAnalyticsKeyphrasesResponse response;
        try {
            response = httpClient.post(keyPhrasesEndpoint, request, authHeaders, AzureTextAnalyticsKeyphrasesResponse.class);
        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            return emptyList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response
            .getDocuments()
            .get(0)
            .getKeyPhrases();
    }

    @Override
    public List<String> fetchEntities(String text, String language) {
        AzureTextAnalyticsRequest request = buildTextAnalyticsRequest(text, language);

        AzureTextAnalyticsEntitiesResponse response;
        try {
            response = httpClient.post(entitiesEndpoint, request, authHeaders, AzureTextAnalyticsEntitiesResponse.class);
        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            return emptyList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response
            .getDocuments()
            .get(0)
            .getEntities()
            .stream()
            .map(AzureTextAnalyticsEntity::getName)
            .collect(toList());
    }

    private AzureTextAnalyticsRequest buildTextAnalyticsRequest(String text, String language) {
        AzureTextAnalyticsDocument document = new AzureTextAnalyticsDocument();
        document.setId("1");
        document.setText(text);
        document.setLanguage(language);

        List<AzureTextAnalyticsDocument> documents = new ArrayList<>();
        documents.add(document);

        AzureTextAnalyticsRequest body = new AzureTextAnalyticsRequest();
        body.setDocuments(documents);
        return body;
    }
}
