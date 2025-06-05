package com.juaracoding.cicd.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Config Tests")
class ConfigTest {

    private MockedStatic<EnvLoader> envLoaderMock;
    
    @BeforeEach
    void setUp() {
        envLoaderMock = mockStatic(EnvLoader.class);
    }
    
    @AfterEach
    void tearDown() {
        envLoaderMock.close();
    }
    
    @Test
    @DisplayName("Should get API key from EnvLoader when available")
    void shouldGetApiKeyFromEnvLoaderWhenAvailable() {
        // Given
        String expectedApiKey = "test-api-key-123";
        envLoaderMock.when(() -> EnvLoader.getEnv("GEMINI_API_KEY")).thenReturn(expectedApiKey);
        
        // When
        String apiKey = Config.getApiKey();
        
        // Then
        assertThat(apiKey).isEqualTo(expectedApiKey);
        envLoaderMock.verify(() -> EnvLoader.getEnv("GEMINI_API_KEY"));
    }
    
    @Test
    @DisplayName("Should throw exception when API key is null")
    void shouldThrowExceptionWhenApiKeyIsNull() {
        // Given
        envLoaderMock.when(() -> EnvLoader.getEnv("GEMINI_API_KEY")).thenReturn(null);
        
        // When & Then
        assertThatThrownBy(() -> Config.getApiKey())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("GEMINI_API_KEY not found");
    }
    
    @Test
    @DisplayName("Should throw exception when API key is empty")
    void shouldThrowExceptionWhenApiKeyIsEmpty() {
        // Given
        envLoaderMock.when(() -> EnvLoader.getEnv("GEMINI_API_KEY")).thenReturn("");
        
        // When & Then
        assertThatThrownBy(() -> Config.getApiKey())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("GEMINI_API_KEY not found");
    }
    
    @Test
    @DisplayName("Should throw exception when API key is only whitespace")
    void shouldThrowExceptionWhenApiKeyIsOnlyWhitespace() {
        // Given
        envLoaderMock.when(() -> EnvLoader.getEnv("GEMINI_API_KEY")).thenReturn("   ");
        
        // When & Then
        assertThatThrownBy(() -> Config.getApiKey())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("GEMINI_API_KEY not found");
    }
    
    @Test
    @DisplayName("Should construct correct API URL with valid API key")
    void shouldConstructCorrectApiUrlWithValidApiKey() {
        // Given
        String apiKey = "test-api-key-123";
        envLoaderMock.when(() -> EnvLoader.getEnv("GEMINI_API_KEY")).thenReturn(apiKey);
        
        // When
        String apiUrl = Config.getApiUrl();
        
        // Then
        assertThat(apiUrl).isNotNull();
        assertThat(apiUrl).contains("generativelanguage.googleapis.com");
        assertThat(apiUrl).contains("gemini-1.5-flash");
        assertThat(apiUrl).contains("generateContent");
        assertThat(apiUrl).contains("key=" + apiKey);
    }
    
    @Test
    @DisplayName("Should return true when API key is set")
    void shouldReturnTrueWhenApiKeyIsSet() {
        // Given
        envLoaderMock.when(() -> EnvLoader.isEnvSet("GEMINI_API_KEY")).thenReturn(true);
        
        // When
        boolean isSet = Config.isApiKeySet();
        
        // Then
        assertThat(isSet).isTrue();
        envLoaderMock.verify(() -> EnvLoader.isEnvSet("GEMINI_API_KEY"));
    }
    
    @Test
    @DisplayName("Should return false when API key is not set")
    void shouldReturnFalseWhenApiKeyIsNotSet() {
        // Given
        envLoaderMock.when(() -> EnvLoader.isEnvSet("GEMINI_API_KEY")).thenReturn(false);
        
        // When
        boolean isSet = Config.isApiKeySet();
        
        // Then
        assertThat(isSet).isFalse();
        envLoaderMock.verify(() -> EnvLoader.isEnvSet("GEMINI_API_KEY"));
    }
    
    @Test
    @DisplayName("Should return system environment source when system env is available")
    void shouldReturnSystemEnvironmentSourceWhenSystemEnvIsAvailable() {
        // Given
        try (MockedStatic<System> systemMock = mockStatic(System.class)) {
            systemMock.when(() -> System.getenv("GEMINI_API_KEY")).thenReturn("system-api-key");
            
            // When
            String source = Config.getApiKeySource();
            
            // Then
            assertThat(source).isEqualTo("System Environment Variable");
        }
    }
    
    @Test
    @DisplayName("Should return .env file source when system env is not available")
    void shouldReturnEnvFileSourceWhenSystemEnvIsNotAvailable() {
        // Given
        try (MockedStatic<System> systemMock = mockStatic(System.class)) {
            systemMock.when(() -> System.getenv("GEMINI_API_KEY")).thenReturn(null);
            
            // When
            String source = Config.getApiKeySource();
            
            // Then
            assertThat(source).isEqualTo(".env file");
        }
    }
    
    @Test
    @DisplayName("Should return .env file source when system env is empty")
    void shouldReturnEnvFileSourceWhenSystemEnvIsEmpty() {
        // Given
        try (MockedStatic<System> systemMock = mockStatic(System.class)) {
            systemMock.when(() -> System.getenv("GEMINI_API_KEY")).thenReturn("");
            
            // When
            String source = Config.getApiKeySource();
            
            // Then
            assertThat(source).isEqualTo(".env file");
        }
    }
    
    @Test
    @DisplayName("Should handle very long API key")
    void shouldHandleVeryLongApiKey() {
        // Given
        String longApiKey = "a".repeat(1000);
        envLoaderMock.when(() -> EnvLoader.getEnv("GEMINI_API_KEY")).thenReturn(longApiKey);
        
        // When
        String apiKey = Config.getApiKey();
        String apiUrl = Config.getApiUrl();
        
        // Then
        assertThat(apiKey).isEqualTo(longApiKey);
        assertThat(apiUrl).contains(longApiKey);
    }
    
    @Test
    @DisplayName("Should handle API key with special characters")
    void shouldHandleApiKeyWithSpecialCharacters() {
        // Given
        String specialApiKey = "API-key_123.test@domain";
        envLoaderMock.when(() -> EnvLoader.getEnv("GEMINI_API_KEY")).thenReturn(specialApiKey);
        
        // When
        String apiKey = Config.getApiKey();
        String apiUrl = Config.getApiUrl();
        
        // Then
        assertThat(apiKey).isEqualTo(specialApiKey);
        assertThat(apiUrl).contains(specialApiKey);
    }
    
    @Test
    @DisplayName("Should maintain consistent API URL format")
    void shouldMaintainConsistentApiUrlFormat() {
        // Given
        String apiKey = "test-key";
        envLoaderMock.when(() -> EnvLoader.getEnv("GEMINI_API_KEY")).thenReturn(apiKey);
        
        // When
        String apiUrl1 = Config.getApiUrl();
        String apiUrl2 = Config.getApiUrl();
        
        // Then
        assertThat(apiUrl1).isEqualTo(apiUrl2);
        assertThat(apiUrl1).startsWith("https://");
        assertThat(apiUrl1).endsWith("?key=" + apiKey);
    }
    
    @Test
    @DisplayName("Should call EnvLoader methods correctly")
    void shouldCallEnvLoaderMethodsCorrectly() {
        // Given
        String apiKey = "test-api-key";
        envLoaderMock.when(() -> EnvLoader.getEnv("GEMINI_API_KEY")).thenReturn(apiKey);
        envLoaderMock.when(() -> EnvLoader.isEnvSet("GEMINI_API_KEY")).thenReturn(true);
        
        // When
        Config.getApiKey();
        Config.isApiKeySet();
        
        // Then
        envLoaderMock.verify(() -> EnvLoader.getEnv("GEMINI_API_KEY"));
        envLoaderMock.verify(() -> EnvLoader.isEnvSet("GEMINI_API_KEY"));
    }
}
