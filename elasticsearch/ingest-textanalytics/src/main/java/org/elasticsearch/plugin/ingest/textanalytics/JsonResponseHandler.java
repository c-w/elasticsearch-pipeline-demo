package org.elasticsearch.plugin.ingest.textanalytics;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

class JsonResponseHandler implements ResponseHandler<JsonElement> {
    @Override
    public JsonElement handleResponse(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status < 200 || status >= 300) {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }

        HttpEntity entity = response.getEntity();
        String body = EntityUtils.toString(entity);
        return new JsonParser().parse(body);
    }
}
