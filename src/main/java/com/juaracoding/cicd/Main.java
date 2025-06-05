package com.juaracoding.cicd;

public class Main {
    public static void main(String[] args) {
        ChatBot chatBot = new ChatBot();
        
        // Add shutdown hook to gracefully close resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down gracefully...");
            chatBot.shutdown();
        }));
        
        // Start the chatbot
        chatBot.start();
    }
}
