package com.juaracoding.cicd.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {
    private static Map<String, String> envVars = new HashMap<>();
    private static boolean loaded = false;
    
    public static void loadEnvFile() {
        if (loaded) return;
        
        String envFile = ".env";
        if (!Files.exists(Paths.get(envFile))) {
            loaded = true;
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse key=value
                int equalIndex = line.indexOf('=');
                if (equalIndex != -1) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();
                    
                    // Remove quotes if present
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }
                    
                    envVars.put(key, value);
                }
            }
            loaded = true;
        } catch (IOException e) {
            System.err.println("Warning: Could not read .env file: " + e.getMessage());
            loaded = true;
        }
    }
    
    public static String getEnv(String key) {
        // First try system environment variable
        String systemValue = System.getenv(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            return systemValue;
        }
        
        // Then try .env file
        loadEnvFile();
        return envVars.get(key);
    }
    
    public static boolean isEnvSet(String key) {
        String value = getEnv(key);
        return value != null && !value.isEmpty();
    }
}
