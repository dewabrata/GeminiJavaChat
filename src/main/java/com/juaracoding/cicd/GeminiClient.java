package com.juaracoding.cicd;

import com.google.gson.Gson;
import com.juaracoding.cicd.config.Config;
import com.juaracoding.cicd.models.GeminiRequest;
import com.juaracoding.cicd.models.GeminiResponse;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GeminiClient {
    private OkHttpClient client;
    private Gson gson;
    
    public GeminiClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }
    
    public String sendMessage(String message, String systemPrompt) throws IOException {
        // Create request body with system prompt and user message
        String fullMessage = systemPrompt + "\n\nUser: " + message;
        
        GeminiRequest.Part part = new GeminiRequest.Part(fullMessage);
        List<GeminiRequest.Part> parts = new ArrayList<>();
        parts.add(part);
        
        GeminiRequest.Content content = new GeminiRequest.Content(parts);
        List<GeminiRequest.Content> contents = new ArrayList<>();
        contents.add(content);
        
        GeminiRequest.GenerationConfig config = new GeminiRequest.GenerationConfig();
        GeminiRequest request = new GeminiRequest(contents, config);
        
        // Convert to JSON
        String jsonBody = gson.toJson(request);
        
        // Create HTTP request
        RequestBody body = RequestBody.create(
            jsonBody, MediaType.get("application/json; charset=utf-8")
        );
        
        Request httpRequest = new Request.Builder()
                .url(Config.getApiUrl())
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        
        // Execute request
        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + 
                                    " - " + response.message());
            }
            
            String responseBody = response.body().string();
            GeminiResponse geminiResponse = gson.fromJson(responseBody, GeminiResponse.class);
            
            if (geminiResponse.getCandidates() != null && 
                !geminiResponse.getCandidates().isEmpty() &&
                geminiResponse.getCandidates().get(0).getContent() != null &&
                geminiResponse.getCandidates().get(0).getContent().getParts() != null &&
                !geminiResponse.getCandidates().get(0).getContent().getParts().isEmpty()) {
                
                return geminiResponse.getCandidates().get(0)
                    .getContent().getParts().get(0).getText();
            } else {
                throw new IOException("Invalid response format from Gemini API");
            }
        }
    }
    
    public boolean testConnection() {
        try {
            sendMessage("Hello", "You are a helpful assistant.");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void close() {
        if (client != null) {
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
        }
    }
}
