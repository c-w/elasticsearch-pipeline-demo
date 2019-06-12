package org.elasticsearch.plugin.ingest.sentiment;

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
import java.util.Map;
import java.util.Optional;

class AzureTextAnalyticsClient implements SentimentAnalysis {
    private final JsonHttpClient httpClient;
    private final Map<String, String> authHeaders;
    private final URI sentimentEndpoint;

    AzureTextAnalyticsClient(JsonHttpClient httpClient, String accessKey, URL endpoint) throws MalformedURLException,
        URISyntaxException {
        this.httpClient = httpClient;

        this.authHeaders = new HashMap<>();
        this.authHeaders.put("Ocp-Apim-Subscription-Key", accessKey);

        this.sentimentEndpoint = new URL(endpoint, "/text/analytics/v2.1/sentiment").toURI();
    }

    @Override
    public Optional<Double> fetchSentiment(String text, String language) {
        JsonObject request = buildSentimentRequest(text, language);

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

    private JsonObject buildSentimentRequest(String text, String language) {
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
}
