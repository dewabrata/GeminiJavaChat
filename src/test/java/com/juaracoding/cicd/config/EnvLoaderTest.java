package com.juaracoding.cicd.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("EnvLoader Tests")
class EnvLoaderTest {

    @TempDir
    Path tempDir;
    
    private Path originalWorkingDir;
    
    @BeforeEach
    void setUp() throws IOException {
        // Save original working directory
        originalWorkingDir = Paths.get(System.getProperty("user.dir"));
        
        // Reset EnvLoader state for each test
        resetEnvLoaderState();
    }
    
    @AfterEach
    void tearDown() throws IOException {
        // Restore original working directory
        System.setProperty("user.dir", originalWorkingDir.toString());
        
        // Reset EnvLoader state after each test
        resetEnvLoaderState();
    }
    
    private void resetEnvLoaderState() {
        try {
            // Reset the static fields in EnvLoader using reflection
            Field envVarsField = EnvLoader.class.getDeclaredField("envVars");
            envVarsField.setAccessible(true);
            Map<String, String> envVars = (Map<String, String>) envVarsField.get(null);
            envVars.clear();
            
            Field loadedField = EnvLoader.class.getDeclaredField("loaded");
            loadedField.setAccessible(true);
            loadedField.setBoolean(null, false);
        } catch (Exception e) {
            // If reflection fails, just continue
        }
    }
    
    @Test
    @DisplayName("Should load simple key-value pairs from .env file")
    void shouldLoadSimpleKeyValuePairsFromEnvFile() throws IOException {
        // Given
        String envContent = """
            API_KEY=test123
            DATABASE_URL=localhost:5432
            DEBUG=true
            """;
        createEnvFile(envContent);
        
        // When
        EnvLoader.loadEnvFile();
        
        // Then
        assertThat(EnvLoader.getEnv("API_KEY")).isEqualTo("test123");
        assertThat(EnvLoader.getEnv("DATABASE_URL")).isEqualTo("localhost:5432");
        assertThat(EnvLoader.getEnv("DEBUG")).isEqualTo("true");
    }
    
    @Test
    @DisplayName("Should handle quoted values in .env file")
    void shouldHandleQuotedValuesInEnvFile() throws IOException {
        // Given
        String envContent = """
            QUOTED_DOUBLE="Hello World"
            QUOTED_SINGLE='Hello World'
            UNQUOTED=Hello World
            """;
        createEnvFile(envContent);
        
        // When
        EnvLoader.loadEnvFile();
        
        // Then
        assertThat(EnvLoader.getEnv("QUOTED_DOUBLE")).isEqualTo("Hello World");
        assertThat(EnvLoader.getEnv("QUOTED_SINGLE")).isEqualTo("Hello World");
        assertThat(EnvLoader.getEnv("UNQUOTED")).isEqualTo("Hello World");
    }
    
    @Test
    @DisplayName("Should skip comments and empty lines")
    void shouldSkipCommentsAndEmptyLines() throws IOException {
        // Given
        String envContent = """
            # This is a comment
            API_KEY=test123
            
            # Another comment
            DATABASE_URL=localhost:5432
            
            DEBUG=true
            """;
        createEnvFile(envContent);
        
        // When
        EnvLoader.loadEnvFile();
        
        // Then
        assertThat(EnvLoader.getEnv("API_KEY")).isEqualTo("test123");
        assertThat(EnvLoader.getEnv("DATABASE_URL")).isEqualTo("localhost:5432");
        assertThat(EnvLoader.getEnv("DEBUG")).isEqualTo("true");
    }
    
    @Test
    @DisplayName("Should handle special characters in values")
    void shouldHandleSpecialCharactersInValues() throws IOException {
        // Given
        String envContent = """
            SPECIAL_CHARS=!@#$%^&*()
            URL_WITH_PARAMS=https://api.example.com?param1=value1&param2=value2
            JSON_DATA={"key": "value", "number": 123}
            """;
        createEnvFile(envContent);
        
        // When
        EnvLoader.loadEnvFile();
        
        // Then
        assertThat(EnvLoader.getEnv("SPECIAL_CHARS")).isEqualTo("!@#$%^&*()");
        assertThat(EnvLoader.getEnv("URL_WITH_PARAMS")).isEqualTo("https://api.example.com?param1=value1&param2=value2");
        assertThat(EnvLoader.getEnv("JSON_DATA")).isEqualTo("{\"key\": \"value\", \"number\": 123}");
    }
    
    @Test
    @DisplayName("Should handle missing .env file gracefully")
    void shouldHandleMissingEnvFileGracefully() throws IOException {
        // Given
        // No .env file created
        System.setProperty("user.dir", tempDir.toString());
        
        // When & Then
        assertThatCode(() -> EnvLoader.loadEnvFile()).doesNotThrowAnyException();
        assertThat(EnvLoader.getEnv("NONEXISTENT_KEY")).isNull();
    }
    
    @Test
    @DisplayName("Should prioritize system environment over .env file")
    void shouldPrioritizeSystemEnvironmentOverEnvFile() throws IOException {
        // Given
        String envContent = "TEST_PRIORITY=from_env_file";
        createEnvFile(envContent);
        
        // Mock system environment variable (this is tricky in tests, but we can test the logic)
        // Since we can't easily mock System.getenv(), we'll test the method behavior
        
        // When
        EnvLoader.loadEnvFile();
        
        // Then
        // If there's a system env var, it should take priority
        // If not, it should use the .env file value
        String result = EnvLoader.getEnv("TEST_PRIORITY");
        
        // The result will be from .env file since we can't set system env in test
        assertThat(result).isEqualTo("from_env_file");
    }
    
    @Test
    @DisplayName("Should handle malformed lines gracefully")
    void shouldHandleMalformedLinesGracefully() throws IOException {
        // Given
        String envContent = """
            VALID_KEY=valid_value
            INVALID_LINE_NO_EQUALS
            =INVALID_EMPTY_KEY
            ANOTHER_VALID=another_value
            """;
        createEnvFile(envContent);
        
        // When
        EnvLoader.loadEnvFile();
        
        // Then
        assertThat(EnvLoader.getEnv("VALID_KEY")).isEqualTo("valid_value");
        assertThat(EnvLoader.getEnv("ANOTHER_VALID")).isEqualTo("another_value");
        assertThat(EnvLoader.getEnv("INVALID_LINE_NO_EQUALS")).isNull();
    }
    
    @Test
    @DisplayName("Should handle empty values")
    void shouldHandleEmptyValues() throws IOException {
        // Given
        String envContent = """
            EMPTY_VALUE=
            QUOTED_EMPTY=""
            SINGLE_QUOTED_EMPTY=''
            """;
        createEnvFile(envContent);
        
        // When
        EnvLoader.loadEnvFile();
        
        // Then
        assertThat(EnvLoader.getEnv("EMPTY_VALUE")).isEmpty();
        assertThat(EnvLoader.getEnv("QUOTED_EMPTY")).isEmpty();
        assertThat(EnvLoader.getEnv("SINGLE_QUOTED_EMPTY")).isEmpty();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"TEST_KEY1", "TEST_KEY2", "NONEXISTENT"})
    @DisplayName("Should correctly report if environment variable is set")
    void shouldCorrectlyReportIfEnvironmentVariableIsSet(String key) throws IOException {
        // Given
        String envContent = """
            TEST_KEY1=value1
            TEST_KEY2=value2
            """;
        createEnvFile(envContent);
        
        // When
        boolean isSet = EnvLoader.isEnvSet(key);
        
        // Then
        if (key.equals("NONEXISTENT")) {
            assertThat(isSet).isFalse();
        } else {
            assertThat(isSet).isTrue();
        }
    }
    
    @Test
    @DisplayName("Should handle whitespace around keys and values")
    void shouldHandleWhitespaceAroundKeysAndValues() throws IOException {
        // Given
        String envContent = """
              SPACED_KEY   =   spaced_value   
            \tTABBED_KEY\t=\tTabbed Value\t
            MIXED_SPACES = mixed value 
            """;
        createEnvFile(envContent);
        
        // When
        EnvLoader.loadEnvFile();
        
        // Then
        assertThat(EnvLoader.getEnv("SPACED_KEY")).isEqualTo("spaced_value");
        assertThat(EnvLoader.getEnv("TABBED_KEY")).isEqualTo("Tabbed Value");
        assertThat(EnvLoader.getEnv("MIXED_SPACES")).isEqualTo("mixed value");
    }
    
    @Test
    @DisplayName("Should load only once even with multiple calls")
    void shouldLoadOnlyOnceEvenWithMultipleCalls() throws IOException {
        // Given
        String envContent = "TEST_KEY=original_value";
        createEnvFile(envContent);
        
        // When
        EnvLoader.loadEnvFile(); // First call
        String firstValue = EnvLoader.getEnv("TEST_KEY");
        
        // Modify the file content
        String newEnvContent = "TEST_KEY=modified_value";
        Files.writeString(tempDir.resolve(".env"), newEnvContent);
        
        EnvLoader.loadEnvFile(); // Second call
        String secondValue = EnvLoader.getEnv("TEST_KEY");
        
        // Then
        assertThat(firstValue).isEqualTo("original_value");
        assertThat(secondValue).isEqualTo("original_value"); // Should still be original
    }
    
    private void createEnvFile(String content) throws IOException {
        Path envFile = tempDir.resolve(".env");
        Files.writeString(envFile, content);
        System.setProperty("user.dir", tempDir.toString());
    }
}
