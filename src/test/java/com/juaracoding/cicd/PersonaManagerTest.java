package com.juaracoding.cicd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PersonaManager Tests")
class PersonaManagerTest {

    private PersonaManager personaManager;
    
    @BeforeEach
    void setUp() {
        personaManager = new PersonaManager();
    }
    
    @Test
    @DisplayName("Should initialize with default assistant persona")
    void shouldInitializeWithDefaultAssistantPersona() {
        // When & Then
        assertThat(personaManager.getCurrentPersona()).isEqualTo("assistant");
        assertThat(personaManager.getCurrentPersonaCapitalized()).isEqualTo("Assistant");
    }
    
    @Test
    @DisplayName("Should have all expected personas available")
    void shouldHaveAllExpectedPersonasAvailable() {
        // When
        Set<String> availablePersonas = personaManager.getAvailablePersonas();
        
        // Then
        assertThat(availablePersonas).containsExactlyInAnyOrder(
            "assistant", "creative", "developer", "teacher", "friend"
        );
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"assistant", "creative", "developer", "teacher", "friend"})
    @DisplayName("Should successfully set valid personas")
    void shouldSuccessfullySetValidPersonas(String personaName) {
        // When
        boolean result = personaManager.setPersona(personaName);
        
        // Then
        assertThat(result).isTrue();
        assertThat(personaManager.getCurrentPersona()).isEqualTo(personaName);
        assertThat(personaManager.getCurrentPersonaCapitalized())
            .isEqualTo(personaName.substring(0, 1).toUpperCase() + personaName.substring(1));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"ASSISTANT", "Creative", "DEVELOPER", "Teacher", "FRIEND"})
    @DisplayName("Should handle case insensitive persona names")
    void shouldHandleCaseInsensitivePersonaNames(String personaName) {
        // When
        boolean result = personaManager.setPersona(personaName);
        
        // Then
        assertThat(result).isTrue();
        assertThat(personaManager.getCurrentPersona()).isEqualTo(personaName.toLowerCase());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"invalid", "robot", "ai", "chatbot", "unknown"})
    @DisplayName("Should reject invalid persona names")
    void shouldRejectInvalidPersonaNames(String invalidPersona) {
        // Given
        String originalPersona = personaManager.getCurrentPersona();
        
        // When
        boolean result = personaManager.setPersona(invalidPersona);
        
        // Then
        assertThat(result).isFalse();
        assertThat(personaManager.getCurrentPersona()).isEqualTo(originalPersona);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should reject null and empty persona names")
    void shouldRejectNullAndEmptyPersonaNames(String invalidPersona) {
        // Given
        String originalPersona = personaManager.getCurrentPersona();
        
        // When
        boolean result = personaManager.setPersona(invalidPersona);
        
        // Then
        assertThat(result).isFalse();
        assertThat(personaManager.getCurrentPersona()).isEqualTo(originalPersona);
    }
    
    @Test
    @DisplayName("Should provide system prompt for assistant persona")
    void shouldProvideSystemPromptForAssistantPersona() {
        // Given
        personaManager.setPersona("assistant");
        
        // When
        String systemPrompt = personaManager.getSystemPrompt();
        
        // Then
        assertThat(systemPrompt).isNotNull();
        assertThat(systemPrompt).isNotEmpty();
        assertThat(systemPrompt).contains("AI assistant");
        assertThat(systemPrompt).contains("helpful");
    }
    
    @Test
    @DisplayName("Should provide system prompt for creative persona")
    void shouldProvideSystemPromptForCreativePersona() {
        // Given
        personaManager.setPersona("creative");
        
        // When
        String systemPrompt = personaManager.getSystemPrompt();
        
        // Then
        assertThat(systemPrompt).isNotNull();
        assertThat(systemPrompt).isNotEmpty();
        assertThat(systemPrompt).contains("kreatif");
        assertThat(systemPrompt).contains("imajinatif");
    }
    
    @Test
    @DisplayName("Should provide system prompt for developer persona")
    void shouldProvideSystemPromptForDeveloperPersona() {
        // Given
        personaManager.setPersona("developer");
        
        // When
        String systemPrompt = personaManager.getSystemPrompt();
        
        // Then
        assertThat(systemPrompt).isNotNull();
        assertThat(systemPrompt).isNotEmpty();
        assertThat(systemPrompt).contains("software developer");
        assertThat(systemPrompt).contains("programming");
    }
    
    @Test
    @DisplayName("Should provide system prompt for teacher persona")
    void shouldProvideSystemPromptForTeacherPersona() {
        // Given
        personaManager.setPersona("teacher");
        
        // When
        String systemPrompt = personaManager.getSystemPrompt();
        
        // Then
        assertThat(systemPrompt).isNotNull();
        assertThat(systemPrompt).isNotEmpty();
        assertThat(systemPrompt).contains("guru");
        assertThat(systemPrompt).contains("sabar");
    }
    
    @Test
    @DisplayName("Should provide system prompt for friend persona")
    void shouldProvideSystemPromptForFriendPersona() {
        // Given
        personaManager.setPersona("friend");
        
        // When
        String systemPrompt = personaManager.getSystemPrompt();
        
        // Then
        assertThat(systemPrompt).isNotNull();
        assertThat(systemPrompt).isNotEmpty();
        assertThat(systemPrompt).contains("teman");
        assertThat(systemPrompt).contains("ramah");
    }
    
    @Test
    @DisplayName("Should validate persona correctly")
    void shouldValidatePersonaCorrectly() {
        // Valid personas
        assertThat(personaManager.isValidPersona("assistant")).isTrue();
        assertThat(personaManager.isValidPersona("creative")).isTrue();
        assertThat(personaManager.isValidPersona("developer")).isTrue();
        assertThat(personaManager.isValidPersona("teacher")).isTrue();
        assertThat(personaManager.isValidPersona("friend")).isTrue();
        
        // Case insensitive
        assertThat(personaManager.isValidPersona("ASSISTANT")).isTrue();
        assertThat(personaManager.isValidPersona("Creative")).isTrue();
        
        // Invalid personas
        assertThat(personaManager.isValidPersona("invalid")).isFalse();
        assertThat(personaManager.isValidPersona("robot")).isFalse();
        assertThat(personaManager.isValidPersona("")).isFalse();
        assertThat(personaManager.isValidPersona(null)).isFalse();
    }
    
    @Test
    @DisplayName("Should maintain persona state across multiple operations")
    void shouldMaintainPersonaStateAcrossMultipleOperations() {
        // Given
        assertThat(personaManager.getCurrentPersona()).isEqualTo("assistant");
        
        // When
        personaManager.setPersona("creative");
        String prompt1 = personaManager.getSystemPrompt();
        String persona1 = personaManager.getCurrentPersona();
        
        personaManager.setPersona("developer");
        String prompt2 = personaManager.getSystemPrompt();
        String persona2 = personaManager.getCurrentPersona();
        
        // Then
        assertThat(persona1).isEqualTo("creative");
        assertThat(prompt1).contains("kreatif");
        
        assertThat(persona2).isEqualTo("developer");
        assertThat(prompt2).contains("software developer");
        assertThat(prompt2).isNotEqualTo(prompt1);
    }
    
    @Test
    @DisplayName("Should handle multiple persona switches correctly")
    void shouldHandleMultiplePersonaSwitchesCorrectly() {
        // Test sequence of persona changes
        String[] personas = {"creative", "developer", "teacher", "friend", "assistant"};
        
        for (String persona : personas) {
            // When
            boolean result = personaManager.setPersona(persona);
            
            // Then
            assertThat(result).isTrue();
            assertThat(personaManager.getCurrentPersona()).isEqualTo(persona);
            assertThat(personaManager.getSystemPrompt()).isNotNull();
            assertThat(personaManager.getSystemPrompt()).isNotEmpty();
        }
    }
    
    @Test
    @DisplayName("Should handle special characters in persona validation")
    void shouldHandleSpecialCharactersInPersonaValidation() {
        // Given
        String[] invalidPersonas = {
            "assistant!", "creative@", "developer#", 
            "teacher$", "friend%", "persona with spaces"
        };
        
        // When & Then
        for (String invalidPersona : invalidPersonas) {
            assertThat(personaManager.isValidPersona(invalidPersona)).isFalse();
            assertThat(personaManager.setPersona(invalidPersona)).isFalse();
        }
    }
    
    @Test
    @DisplayName("Should provide unique system prompts for each persona")
    void shouldProvideUniqueSystemPromptsForEachPersona() {
        // Given
        Set<String> availablePersonas = personaManager.getAvailablePersonas();
        
        // When
        Set<String> systemPrompts = availablePersonas.stream()
            .map(persona -> {
                personaManager.setPersona(persona);
                return personaManager.getSystemPrompt();
            })
            .collect(java.util.stream.Collectors.toSet());
        
        // Then
        assertThat(systemPrompts).hasSize(availablePersonas.size());
    }
}
