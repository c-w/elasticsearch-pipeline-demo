package org.elasticsearch.plugin.ingest.textanalytics;

import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsEntities;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsEntitiesResponse;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsEntity;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsKeyphrases;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsKeyphrasesResponse;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsSentiment;
import org.elasticsearch.plugin.ingest.textanalytics.dto.AzureTextAnalyticsSentimentResponse;
import org.elasticsearch.test.ESTestCase;
import org.junit.Before;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
        when(httpClient.post(any(), any(), any(), any())).thenReturn(sampleSentimentResponse());

        Optional<Double> sentiment = textAnalyticsClient.fetchSentiment("text", "language");

        assertTrue(sentiment.isPresent());
        assertEquals(0.92, sentiment.get(), 0.001);
    }

    public void testSentimentRequestTimesOut() throws Exception {
        when(httpClient.post(any(), any(), any(), any())).thenThrow(new SocketTimeoutException("timeout"));

        Optional<Double> sentiment = textAnalyticsClient.fetchSentiment("text", "language");

        assertFalse(sentiment.isPresent());
    }

    public void testKeyPhrasesRequestSucceeds() throws Exception {
        when(httpClient.post(any(), any(), any(), any())).thenReturn(sampleKeyphrasesResponse());

        List<String> keyphrases = textAnalyticsClient.fetchKeyPhrases("text", "language");

        assertEquals(2, keyphrases.size());
        assertEquals("world", keyphrases.get(0));
        assertEquals("input text", keyphrases.get(1));
    }

    public void testkeyPhrasesRequestTimesOut() throws Exception {
        when(httpClient.post(any(), any(), any(), any())).thenThrow(new SocketTimeoutException("timeout"));

        List<String> keyPhrases = textAnalyticsClient.fetchKeyPhrases("text", "language");

        assertTrue(keyPhrases.isEmpty());
    }

    public void testEntitiesRequestSucceeds() throws Exception {
        when(httpClient.post(any(), any(), any(), any())).thenReturn(sampleEntitiesResponse());

        List<String> entities = textAnalyticsClient.fetchEntities("text", "language");

        assertEquals(1, entities.size());
        assertEquals("world", entities.get(0));
    }

    public void testEntitiesRequestTimesOut() throws Exception {
        when(httpClient.post(any(), any(), any(), any())).thenThrow(new SocketTimeoutException("timeout"));

        List<String> entities = textAnalyticsClient.fetchEntities("text", "language");

        assertTrue(entities.isEmpty());
    }

    private static AzureTextAnalyticsSentimentResponse sampleSentimentResponse() {
        AzureTextAnalyticsSentiment document = new AzureTextAnalyticsSentiment();
        document.setId("1");
        document.setScore(0.92);

        List<AzureTextAnalyticsSentiment> documents = new ArrayList<>();
        documents.add(document);

        AzureTextAnalyticsSentimentResponse response = new AzureTextAnalyticsSentimentResponse();
        response.setDocuments(documents);

        return response;
    }

    private static AzureTextAnalyticsKeyphrasesResponse sampleKeyphrasesResponse() {
        List<String> keyphrases = new ArrayList<>();
        keyphrases.add("world");
        keyphrases.add("input text");

        AzureTextAnalyticsKeyphrases document = new AzureTextAnalyticsKeyphrases();
        document.setId("1");
        document.setKeyPhrases(keyphrases);

        List<AzureTextAnalyticsKeyphrases> documents = new ArrayList<>();
        documents.add(document);

        AzureTextAnalyticsKeyphrasesResponse response = new AzureTextAnalyticsKeyphrasesResponse();
        response.setDocuments(documents);

        return response;
    }

    private static AzureTextAnalyticsEntitiesResponse sampleEntitiesResponse() {
        AzureTextAnalyticsEntity entity = new AzureTextAnalyticsEntity();
        entity.setName("world");

        List<AzureTextAnalyticsEntity> entities = new ArrayList<>();
        entities.add(entity);

        AzureTextAnalyticsEntities document = new AzureTextAnalyticsEntities();
        document.setId("1");
        document.setEntities(entities);

        List<AzureTextAnalyticsEntities> documents = new ArrayList<>();
        documents.add(document);

        AzureTextAnalyticsEntitiesResponse response = new AzureTextAnalyticsEntitiesResponse();
        response.setDocuments(documents);

        return response;
    }
}
