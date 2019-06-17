package org.elasticsearch.plugin.ingest.textanalytics;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
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

    JsonHttpClient(int timeoutSeconds, int retryIntervalSeconds) {
        this(defaultHttpClient(retryIntervalSeconds), defaultRequestConfig(timeoutSeconds));
    }

    <TRequest, TResponse> TResponse post(URI uri, TRequest body, Map<String, String> headers,
                                         Class<TResponse> responseType) throws IOException {
        HttpPost request = new HttpPost(uri);
        request.setConfig(requestConfig);
        request.addHeader("Content-Type", "application/json");
        headers.forEach(request::addHeader);
        request.setEntity(new StringEntity(doPrivileged(() -> new Gson().toJsonTree(body).toString())));

        JsonElement response = doPrivilegedIO(() -> httpClient.execute(request, new JsonResponseHandler()));

        return doPrivileged(() -> new Gson().fromJson(response, responseType));
    }

    private static RequestConfig defaultRequestConfig(int timeoutSeconds) {
        return RequestConfig.custom()
            .setConnectionRequestTimeout(timeoutSeconds * 1000)
            .build();
    }

    private static HttpClient defaultHttpClient(int retryIntervalSeconds) {
        return HttpClientBuilder.create()
            .setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {
                @Override
                public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    return statusCode == 502;
                }

                @Override
                public long getRetryInterval() {
                    return retryIntervalSeconds * 1000;
                }
            })
            .build();
    }

    private static <T> T doPrivileged(PrivilegedExceptionAction<T> action) {
        SpecialPermission.check();

        try {
            return AccessController.doPrivileged(action);
        } catch (PrivilegedActionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private static <T> T doPrivilegedIO(PrivilegedExceptionAction<T> action) throws IOException {
        SpecialPermission.check();

        try {
            return AccessController.doPrivileged(action);
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }
}
