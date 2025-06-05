package com.juaracoding.cicd;

import com.juaracoding.cicd.config.Config;

import java.util.ArrayList;
import java.util.List;

public class ChatBot {
    private CLIInterface cli;
    private PersonaManager personaManager;
    private CommandProcessor commandProcessor;
    private GeminiClient geminiClient;
    private List<String> conversationHistory;
    private boolean isRunning;
    
    public ChatBot() {
        this.cli = new CLIInterface();
        this.personaManager = new PersonaManager();
        this.commandProcessor = new CommandProcessor(personaManager, cli);
        this.geminiClient = new GeminiClient();
        this.conversationHistory = new ArrayList<>();
        this.isRunning = true;
    }
    
    public void start() {
        try {
            // Check if API key is set
            if (!Config.isApiKeySet()) {
                cli.showError("GEMINI_API_KEY tidak ditemukan!");
                cli.showError("Silakan set API key dengan salah satu cara berikut:");
                cli.showError("1. Environment variable: set GEMINI_API_KEY=your_api_key_here");
                cli.showError("2. File .env: tambahkan baris GEMINI_API_KEY=your_api_key_here");
                return;
            }
            
            // Show welcome screen
            cli.showWelcome();
            
            // Test connection
            cli.showLoading();
            if (!geminiClient.testConnection()) {
                cli.showError("Tidak dapat terhubung ke Gemini API. Periksa API key dan koneksi internet Anda.");
                return;
            }
            cli.showSuccess("Terhubung ke Gemini API!");
            cli.showSuccess("API Key source: " + Config.getApiKeySource());
            System.out.println();
            
            // Main chat loop
            while (isRunning) {
                String userInput = cli.getUserInput();
                
                // Handle empty input
                if (userInput.isEmpty()) {
                    continue;
                }
                
                // Check for exit commands
                if (commandProcessor.isExitCommand(userInput)) {
                    break;
                }
                
                // Process commands
                if (userInput.startsWith("/")) {
                    boolean continueLoop = commandProcessor.processCommand(userInput);
                    if (!continueLoop) {
                        break;
                    }
                    continue;
                }
                
                // Handle regular chat message
                handleChatMessage(userInput);
            }
            
        } catch (Exception e) {
            cli.showError("Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }
    
    private void handleChatMessage(String userInput) {
        try {
            // Add to conversation history
            conversationHistory.add("User: " + userInput);
            
            // Show loading animation
            cli.showLoading();
            
            // Get system prompt from current persona
            String systemPrompt = personaManager.getSystemPrompt();
            
            // Send message to Gemini
            String response = geminiClient.sendMessage(userInput, systemPrompt);
            
            // Add response to history
            conversationHistory.add("Assistant: " + response);
            
            // Display response
            cli.showBotResponse(response, personaManager.getCurrentPersonaCapitalized());
            
        } catch (Exception e) {
            cli.showError("Gagal mendapatkan response dari Gemini: " + e.getMessage());
            
            // Check if it's an authentication error
            if (e.getMessage().contains("401") || e.getMessage().contains("403")) {
                cli.showError("API key mungkin tidak valid. Silakan periksa GEMINI_API_KEY Anda.");
            }
        }
    }
    
    private void clearConversationHistory() {
        conversationHistory.clear();
    }
    
    public void shutdown() {
        cli.showGoodbye();
        
        // Close resources
        if (geminiClient != null) {
            geminiClient.close();
        }
        
        if (cli != null) {
            cli.close();
        }
        
        isRunning = false;
    }
    
    // Getter for testing purposes
    public List<String> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }
    
    public PersonaManager getPersonaManager() {
        return personaManager;
    }
}
