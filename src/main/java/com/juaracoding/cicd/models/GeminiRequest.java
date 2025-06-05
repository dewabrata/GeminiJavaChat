package com.juaracoding.cicd.models;

import java.util.List;

public class GeminiRequest {
    private List<Content> contents;
    private GenerationConfig generationConfig;
    
    public GeminiRequest(List<Content> contents, GenerationConfig generationConfig) {
        this.contents = contents;
        this.generationConfig = generationConfig;
    }
    
    // Getters and Setters
    public List<Content> getContents() {
        return contents;
    }
    
    public void setContents(List<Content> contents) {
        this.contents = contents;
    }
    
    public GenerationConfig getGenerationConfig() {
        return generationConfig;
    }
    
    public void setGenerationConfig(GenerationConfig generationConfig) {
        this.generationConfig = generationConfig;
    }
    
    // Inner classes
    public static class Content {
        private List<Part> parts;
        
        public Content(List<Part> parts) {
            this.parts = parts;
        }
        
        public List<Part> getParts() {
            return parts;
        }
        
        public void setParts(List<Part> parts) {
            this.parts = parts;
        }
    }
    
    public static class Part {
        private String text;
        
        public Part(String text) {
            this.text = text;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
    }
    
    public static class GenerationConfig {
        private double temperature;
        private int topK;
        private double topP;
        private int maxOutputTokens;
        
        public GenerationConfig() {
            this.temperature = 0.7;
            this.topK = 40;
            this.topP = 0.95;
            this.maxOutputTokens = 2048;
        }
        
        // Getters and Setters
        public double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }
        
        public int getTopK() {
            return topK;
        }
        
        public void setTopK(int topK) {
            this.topK = topK;
        }
        
        public double getTopP() {
            return topP;
        }
        
        public void setTopP(double topP) {
            this.topP = topP;
        }
        
        public int getMaxOutputTokens() {
            return maxOutputTokens;
        }
        
        public void setMaxOutputTokens(int maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
        }
    }
}
