package org.opennms.plugins.autotask;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.opennms.plugins.autotask.model.AutotaskTicket;
import org.opennms.plugins.autotask.model.AutotaskTicketResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutotaskApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(AutotaskApiClient.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;
    private final String apiIntegrationCode;
    private final String userName;
    private final String secret;

    public AutotaskApiClient(String baseUrl, String apiIntegrationCode, String userName, String secret) {
        this.baseUrl = Objects.requireNonNull(baseUrl);
        this.apiIntegrationCode = Objects.requireNonNull(apiIntegrationCode);
        this.userName = Objects.requireNonNull(userName);
        this.secret = Objects.requireNonNull(secret);
        this.client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    /**
     * Create a new ticket in Autotask
     */
    public CompletableFuture<AutotaskTicketResponse> createTicket(AutotaskTicket ticket) {
        String url = baseUrl + "/Tickets";
        return doPost(url, ticket);
    }

    /**
     * Update an existing ticket in Autotask
     */
    public CompletableFuture<AutotaskTicketResponse> updateTicket(AutotaskTicket ticket) {
        String url = baseUrl + "/Tickets";
        return doPatch(url, ticket);
    }

    /**
     * Get a ticket by ID from Autotask
     */
    public CompletableFuture<AutotaskTicketResponse> getTicket(String ticketId) {
        String url = baseUrl + "/Tickets/" + ticketId;
        return doGet(url);
    }

    private CompletableFuture<AutotaskTicketResponse> doPost(String url, AutotaskTicket ticket) {
        RequestBody body;
        try {
            body = RequestBody.create(JSON, mapper.writeValueAsString(ticket));
        } catch (JsonProcessingException e) {
            CompletableFuture<AutotaskTicketResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to serialize ticket", e));
            return future;
        }

        Request request = buildRequest(url)
                .post(body)
                .build();

        return executeRequest(request);
    }

    private CompletableFuture<AutotaskTicketResponse> doPatch(String url, AutotaskTicket ticket) {
        RequestBody body;
        try {
            body = RequestBody.create(JSON, mapper.writeValueAsString(ticket));
        } catch (JsonProcessingException e) {
            CompletableFuture<AutotaskTicketResponse> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Failed to serialize ticket", e));
            return future;
        }

        Request request = buildRequest(url)
                .patch(body)
                .build();

        return executeRequest(request);
    }

    private CompletableFuture<AutotaskTicketResponse> doGet(String url) {
        Request request = buildRequest(url)
                .get()
                .build();

        return executeRequest(request);
    }

    private Request.Builder buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("ApiIntegrationCode", apiIntegrationCode)
                .addHeader("UserName", userName)
                .addHeader("Secret", secret)
                .addHeader("User-Agent", AutotaskApiClient.class.getCanonicalName());
    }

    private CompletableFuture<AutotaskTicketResponse> executeRequest(Request request) {
        CompletableFuture<AutotaskTicketResponse> future = new CompletableFuture<>();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOG.error("Request failed: {}", e.getMessage(), e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String responseBody = "";
                    ResponseBody body = response.body();
                    if (body != null) {
                        try {
                            responseBody = body.string();
                        } catch (IOException e) {
                            LOG.warn("Failed to read response body", e);
                        } finally {
                            body.close();
                        }
                    }

                    if (!response.isSuccessful()) {
                        String errorMsg = "Request failed with response code: " + response.code() 
                                        + " and body: " + responseBody;
                        LOG.error(errorMsg);
                        future.completeExceptionally(new Exception(errorMsg));
                        return;
                    }

                    try {
                        AutotaskTicketResponse ticketResponse = mapper.readValue(responseBody, AutotaskTicketResponse.class);
                        LOG.debug("Successfully processed response for ticket operation");
                        future.complete(ticketResponse);
                    } catch (JsonProcessingException e) {
                        LOG.error("Failed to parse response: {}", responseBody, e);
                        future.completeExceptionally(new Exception("Failed to parse response", e));
                    }

                } finally {
                    response.close();
                }
            }
        });

        return future;
    }
}