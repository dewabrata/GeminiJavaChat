package com.juaracoding.cicd;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PersonaManager {
    private Map<String, String> personas;
    private String currentPersona;
    
    public PersonaManager() {
        this.personas = new HashMap<>();
        initializePersonas();
        this.currentPersona = "assistant";
    }
    
    private void initializePersonas() {
        personas.put("assistant", 
            "Kamu adalah AI assistant yang helpful, harmless, dan honest. " +
            "Jawab pertanyaan dengan informatif dan membantu pengguna dengan baik.");
        
        personas.put("creative", 
            "Kamu adalah AI yang sangat kreatif dan imajinatif. " +
            "Berikan ide-ide unik, solusi kreatif, dan pendekatan out-of-the-box. " +
            "Gunakan bahasa yang ekspresif dan penuh semangat.");
        
        personas.put("developer", 
            "Kamu adalah senior software developer yang expert dalam berbagai bahasa pemrograman. " +
            "Fokus pada best practices, clean code, dan solusi teknis yang efisien. " +
            "Berikan contoh code dan penjelasan teknis yang detail.");
        
        personas.put("teacher", 
            "Kamu adalah guru yang sabar dan detail dalam menjelaskan. " +
            "Pecah konsep kompleks menjadi langkah-langkah sederhana. " +
            "Berikan contoh, analogi, dan pastikan user memahami sebelum lanjut ke topik berikutnya.");
        
        personas.put("friend", 
            "Kamu adalah teman yang ramah, casual, dan supportive. " +
            "Gunakan bahasa yang santai, emoji sesekali, dan jadilah pendengar yang baik. " +
            "Berikan dukungan moral dan saran yang friendly.");
    }
    
    public boolean setPersona(String personaName) {
        if (personas.containsKey(personaName.toLowerCase())) {
            this.currentPersona = personaName.toLowerCase();
            return true;
        }
        return false;
    }
    
    public String getCurrentPersona() {
        return currentPersona;
    }
    
    public String getCurrentPersonaCapitalized() {
        return currentPersona.substring(0, 1).toUpperCase() + currentPersona.substring(1);
    }
    
    public String getSystemPrompt() {
        return personas.get(currentPersona);
    }
    
    public Set<String> getAvailablePersonas() {
        return personas.keySet();
    }
    
    public boolean isValidPersona(String personaName) {
        return personas.containsKey(personaName.toLowerCase());
    }
}
