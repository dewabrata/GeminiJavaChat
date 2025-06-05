package com.juaracoding.cicd.config;

public class Config {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    
    public static String getApiKey() {
        String apiKey = EnvLoader.getEnv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("GEMINI_API_KEY not found! Please set it as environment variable or in .env file.");
        }
        return apiKey;
    }
    
    public static String getApiUrl() {
        return API_URL + "?key=" + getApiKey();
    }
    
    public static boolean isApiKeySet() {
        return EnvLoader.isEnvSet("GEMINI_API_KEY");
    }
    
    public static String getApiKeySource() {
        // Check if it's from system environment or .env file
        String systemValue = System.getenv("GEMINI_API_KEY");
        if (systemValue != null && !systemValue.isEmpty()) {
            return "System Environment Variable";
        }
        return ".env file";
    }
}
