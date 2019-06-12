package org.elasticsearch.plugin.ingest.sentiment;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.elasticsearch.SpecialPermission;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.Scanner;

class JsonHttpClient {
    private static final JsonParser JSON = new JsonParser();
    private final HttpClient httpClient;
    private final RequestConfig requestConfig;

    private JsonHttpClient(HttpClient httpClient, RequestConfig requestConfig) {
        this.httpClient = httpClient;
        this.requestConfig = requestConfig;
    }

    JsonHttpClient(int timeoutSeconds) {
        this(HttpClientBuilder.create()
                .build(),
            RequestConfig.custom()
                .setConnectionRequestTimeout(timeoutSeconds * 1000)
                .build());
    }

    JsonElement post(URI uri, JsonElement body, Map<String, String> headers) throws IOException {
        HttpPost request = new HttpPost(uri);
        request.setConfig(requestConfig);
        request.addHeader("Content-Type", "application/json");
        headers.forEach(request::addHeader);
        request.setEntity(new StringEntity(body.toString()));

        return executeJsonRequest(request);
    }

    private JsonElement executeJsonRequest(HttpUriRequest request) throws IOException {
        HttpResponse response = executeRawRequest(request);

        String responseBody = readResponseBody(response);
        int status = response.getStatusLine().getStatusCode();
        if (status != 200) {
            throw new RuntimeException("HTTP " + status + "(" + responseBody + ")");
        }

        return JSON.parse(responseBody);
    }

    private HttpResponse executeRawRequest(HttpUriRequest request) throws IOException {
        SpecialPermission.check();

        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<HttpResponse>) () -> httpClient.execute(request));
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }

    private String readResponseBody(HttpResponse response) throws IOException {
        try (InputStream responseStream = response.getEntity().getContent()) {
            Scanner scanner = new Scanner(responseStream, "UTF-8").useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
