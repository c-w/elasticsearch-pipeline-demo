package org.elasticsearch.plugin.ingest.textanalytics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyList;

class AzureTextAnalyticsClient implements TextAnalytics {
    private final JsonHttpClient httpClient;
    private final Map<String, String> authHeaders;
    private final URI keyPhrasesEndpoint;
    private final URI sentimentEndpoint;

    AzureTextAnalyticsClient(JsonHttpClient httpClient, String accessKey, URL endpoint) throws MalformedURLException, URISyntaxException {
        this.httpClient = httpClient;

        this.authHeaders = new HashMap<>();
        this.authHeaders.put("Ocp-Apim-Subscription-Key", accessKey);

        this.keyPhrasesEndpoint = new URL(endpoint, "/text/analytics/v2.1/keyPhrases").toURI();
        this.sentimentEndpoint = new URL(endpoint, "/text/analytics/v2.1/sentiment").toURI();
    }

    @Override
    public Optional<Double> fetchSentiment(String text, String language) {
        JsonObject request = buildTextAnalyticsRequest(text, language);

        JsonElement response;
        try {
            response = httpClient.post(sentimentEndpoint, request, authHeaders);
        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(parseSentimentResponse(response));
    }

    @Override
    public List<String> fetchKeyPhrases(String text, String language) {
        JsonObject request = buildTextAnalyticsRequest(text, language);

        JsonElement response;
        try {
            response = httpClient.post(keyPhrasesEndpoint, request, authHeaders);
        } catch (ConnectTimeoutException | SocketTimeoutException e) {
            return emptyList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parseKeyPhrasesResponse(response);
    }

    private JsonObject buildTextAnalyticsRequest(String text, String language) {
        JsonObject document = new JsonObject();
        document.addProperty("id", "1");
        document.addProperty("text", text);
        document.addProperty("language", language);

        JsonArray documents = new JsonArray();
        documents.add(document);

        JsonObject body = new JsonObject();
        body.add("documents", documents);
        return body;
    }

    private Double parseSentimentResponse(JsonElement response) {
        return response
            .getAsJsonObject()
            .getAsJsonArray("documents")
            .get(0)
            .getAsJsonObject()
            .get("score")
            .getAsDouble();
    }

    private List<String> parseKeyPhrasesResponse(JsonElement response) {
        JsonArray keyPhrases = response
            .getAsJsonObject()
            .getAsJsonArray("documents")
            .get(0)
            .getAsJsonObject()
            .get("keyPhrases")
            .getAsJsonArray();

        return StreamSupport.stream(keyPhrases.spliterator(), false)
            .map(JsonElement::getAsString)
            .collect(Collectors.toList());
    }
}
