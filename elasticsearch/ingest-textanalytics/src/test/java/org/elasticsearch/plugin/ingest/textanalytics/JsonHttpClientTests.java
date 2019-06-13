package org.elasticsearch.plugin.ingest.textanalytics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.elasticsearch.test.ESTestCase;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class JsonHttpClientTests extends ESTestCase {
    public void testCanMakeRequest() throws Exception {
        JsonHttpClient client = new JsonHttpClient(10, 1);

        URI uri = new URI("https://postman-echo.com/post");
        JsonObject body = new JsonObject();
        body.addProperty("echo", "message");
        Map<String, String> headers = new HashMap<>();

        JsonElement response = client.post(uri, body, headers);

        assertEquals(
            "message",
            response.getAsJsonObject()
                .getAsJsonObject("data")
                .get("echo")
                .getAsString());
    }
}
