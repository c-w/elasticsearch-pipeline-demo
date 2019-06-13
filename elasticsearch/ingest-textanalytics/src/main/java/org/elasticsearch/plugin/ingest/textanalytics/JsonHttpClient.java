package org.elasticsearch.plugin.ingest.textanalytics;

import com.google.gson.JsonElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.SpecialPermission;

import java.io.IOException;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;

class JsonHttpClient {
    private final HttpClient httpClient;
    private final RequestConfig requestConfig;

    private JsonHttpClient(HttpClient httpClient, RequestConfig requestConfig) {
        this.httpClient = httpClient;
        this.requestConfig = requestConfig;
    }

    JsonHttpClient(int timeoutSeconds) {
        this(HttpClientBuilder.create()
                .setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {
                    @Override
                    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
                        int statusCode = response.getStatusLine().getStatusCode();
                        return statusCode == 502;
                    }

                    @Override
                    public long getRetryInterval() {
                        return 1000;
                    }
                })
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
        SpecialPermission.check();

        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<JsonElement>) () ->
                httpClient.execute(request, new JsonResponseHandler()));
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }
}
