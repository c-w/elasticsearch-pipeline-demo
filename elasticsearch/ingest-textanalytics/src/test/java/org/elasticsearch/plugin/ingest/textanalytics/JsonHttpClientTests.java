package org.elasticsearch.plugin.ingest.textanalytics;

import org.elasticsearch.test.ESTestCase;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class JsonHttpClientTests extends ESTestCase {
    public void testCanMakeRequest() throws Exception {
        JsonHttpClient client = new JsonHttpClient(10, 1);

        URI uri = new URI("https://postman-echo.com/post");
        TestRequest body = new TestRequest();
        body.setEcho("message");
        Map<String, String> headers = new HashMap<>();

        TestResponse response = client.post(uri, body, headers, TestResponse.class);

        assertEquals("message", response.getData().getEcho());
    }

    @SuppressWarnings("unused,WeakerAccess")
    public static class TestRequest {
        private String echo;

        public String getEcho() {
            return echo;
        }

        public void setEcho(String echo) {
            this.echo = echo;
        }
    }

    @SuppressWarnings("unused,WeakerAccess")
    public static class TestResponse {
        private TestRequest data;

        public TestRequest getData() {
            return data;
        }

        public void setData(TestRequest data) {
            this.data = data;
        }
    }
}
