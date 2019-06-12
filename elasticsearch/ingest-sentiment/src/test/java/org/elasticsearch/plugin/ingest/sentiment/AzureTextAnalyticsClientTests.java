package org.elasticsearch.plugin.ingest.sentiment;

import com.google.gson.JsonParser;
import org.elasticsearch.test.ESTestCase;
import org.junit.Before;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AzureTextAnalyticsClientTests extends ESTestCase {
    private JsonHttpClient httpClient;
    private AzureTextAnalyticsClient textAnalyticsClient;

    @Before
    public void setUpMocks() throws Exception {
        httpClient = mock(JsonHttpClient.class);

        textAnalyticsClient = new AzureTextAnalyticsClient(httpClient, "access-key", new URL("http://endpoint"));
    }

    public void testSentimentRequestSucceeds() throws Exception {
        when(httpClient.post(any(), any(), any())).thenReturn(
            new JsonParser().parse(
                "{" +
                "  \"documents\": [" +
                "    {" +
                "      \"id\": \"1\"," +
                "      \"score\": 0.92" +
                "    }" +
                "  ]," +
                "  \"errrors\": []" +
                "}"));

        Optional<Double> sentiment = textAnalyticsClient.fetchSentiment("text", "language");

        assertTrue(sentiment.isPresent());
        assertEquals(sentiment.get(), 0.92, 0.001);
    }

    public void testSentimentRequestTimesOut() throws Exception {
        when(httpClient.post(any(), any(), any())).thenThrow(new SocketTimeoutException("timeout"));

        Optional<Double> sentiment = textAnalyticsClient.fetchSentiment("text", "language");

        assertFalse(sentiment.isPresent());
    }
}
