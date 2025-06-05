package com.juaracoding.cicd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommandProcessor Tests")
class CommandProcessorTest {

    @Mock
    private PersonaManager mockPersonaManager;
    
    @Mock
    private CLIInterface mockCLI;
    
    private CommandProcessor commandProcessor;
    
    @BeforeEach
    void setUp() {
        commandProcessor = new CommandProcessor(mockPersonaManager, mockCLI);
    }
    
    @Test
    @DisplayName("Should handle help command")
    void shouldHandleHelpCommand() {
        // When
        boolean result = commandProcessor.processCommand("/help");
        
        // Then
        assertThat(result).isTrue();
        verify(mockCLI).showHelp();
        verifyNoInteractions(mockPersonaManager);
    }
    
    @Test
    @DisplayName("Should handle clear command")
    void shouldHandleClearCommand() {
        // When
        boolean result = commandProcessor.processCommand("/clear");
        
        // Then
        assertThat(result).isTrue();
        verify(mockCLI).clearScreen();
        verify(mockCLI).showWelcome();
        verifyNoInteractions(mockPersonaManager);
    }
    
    @Test
    @DisplayName("Should handle list-personas command")
    void shouldHandleListPersonasCommand() {
        // When
        boolean result = commandProcessor.processCommand("/list-personas");
        
        // Then
        assertThat(result).isTrue();
        verify(mockCLI).showPersonas();
        verifyNoInteractions(mockPersonaManager);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"/exit", "/quit"})
    @DisplayName("Should handle exit commands")
    void shouldHandleExitCommands(String exitCommand) {
        // When
        boolean result = commandProcessor.processCommand(exitCommand);
        
        // Then
        assertThat(result).isFalse(); // Should return false to signal exit
        verifyNoInteractions(mockCLI, mockPersonaManager);
    }
    
    @Test
    @DisplayName("Should handle persona command with valid persona")
    void shouldHandlePersonaCommandWithValidPersona() {
        // Given
        when(mockPersonaManager.setPersona("creative")).thenReturn(true);
        when(mockPersonaManager.getCurrentPersonaCapitalized()).thenReturn("Creative");
        
        // When
        boolean result = commandProcessor.processCommand("/persona creative");
        
        // Then
        assertThat(result).isTrue();
        verify(mockPersonaManager).setPersona("creative");
        verify(mockPersonaManager).getCurrentPersonaCapitalized();
        verify(mockCLI).showPersonaChanged("Creative");
    }
    
    @Test
    @DisplayName("Should handle persona command with invalid persona")
    void shouldHandlePersonaCommandWithInvalidPersona() {
        // Given
        when(mockPersonaManager.setPersona("invalid")).thenReturn(false);
        
        // When
        boolean result = commandProcessor.processCommand("/persona invalid");
        
        // Then
        assertThat(result).isTrue();
        verify(mockPersonaManager).setPersona("invalid");
        verify(mockCLI).showError("Persona 'invalid' tidak ditemukan.");
        verify(mockCLI).showPersonas();
    }
    
    @Test
    @DisplayName("Should handle persona command without argument")
    void shouldHandlePersonaCommandWithoutArgument() {
        // When
        boolean result = commandProcessor.processCommand("/persona");
        
        // Then
        assertThat(result).isTrue();
        verify(mockCLI).showError("Silakan specify nama persona. Contoh: /persona creative");
        verify(mockCLI).showPersonas();
        verifyNoInteractions(mockPersonaManager);
    }
    
    @Test
    @DisplayName("Should handle persona command with empty argument")
    void shouldHandlePersonaCommandWithEmptyArgument() {
        // When
        boolean result = commandProcessor.processCommand("/persona ");
        
        // Then
        assertThat(result).isTrue();
        verify(mockCLI).showError("Silakan specify nama persona. Contoh: /persona creative");
        verify(mockCLI).showPersonas();
        verifyNoInteractions(mockPersonaManager);
    }
    
    @Test
    @DisplayName("Should handle persona command with multiple arguments")
    void shouldHandlePersonaCommandWithMultipleArguments() {
        // Given
        when(mockPersonaManager.setPersona("creative")).thenReturn(true);
        when(mockPersonaManager.getCurrentPersonaCapitalized()).thenReturn("Creative");
        
        // When
        boolean result = commandProcessor.processCommand("/persona creative extra arguments");
        
        // Then
        assertThat(result).isTrue();
        verify(mockPersonaManager).setPersona("creative extra arguments");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"/unknown", "/invalid", "/test", "/random"})
    @DisplayName("Should handle unknown commands")
    void shouldHandleUnknownCommands(String unknownCommand) {
        // When
        boolean result = commandProcessor.processCommand(unknownCommand);
        
        // Then
        assertThat(result).isTrue();
        verify(mockCLI).showError(contains("Perintah tidak dikenal"));
        verify(mockCLI).showError(contains(unknownCommand));
        verify(mockCLI).showError(contains("'/help'"));
        verifyNoInteractions(mockPersonaManager);
    }
    
    @Test
    @DisplayName("Should not process non-command input")
    void shouldNotProcessNonCommandInput() {
        // When
        boolean result = commandProcessor.processCommand("hello world");
        
        // Then
        assertThat(result).isFalse(); // Should return false for non-commands
        verifyNoInteractions(mockCLI, mockPersonaManager);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should not process null or empty input")
    void shouldNotProcessNullOrEmptyInput(String input) {
        // When
        boolean result = commandProcessor.processCommand(input);
        
        // Then
        assertThat(result).isFalse(); // Should return false for null/empty
        verifyNoInteractions(mockCLI, mockPersonaManager);
    }
    
    @Test
    @DisplayName("Should handle commands with different case")
    void shouldHandleCommandsWithDifferentCase() {
        // When
        boolean helpResult = commandProcessor.processCommand("/HELP");
        boolean clearResult = commandProcessor.processCommand("/Clear");
        boolean exitResult = commandProcessor.processCommand("/EXIT");
        
        // Then
        assertThat(helpResult).isTrue();
        assertThat(clearResult).isTrue();
        assertThat(exitResult).isFalse();
        
        verify(mockCLI).showHelp();
        verify(mockCLI).clearScreen();
        verify(mockCLI).showWelcome();
    }
    
    @Test
    @DisplayName("Should handle commands with extra whitespace")
    void shouldHandleCommandsWithExtraWhitespace() {
        // When
        boolean helpResult = commandProcessor.processCommand("  /help  ");
        boolean personaResult = commandProcessor.processCommand("  /persona   creative  ");
        
        // Given
        when(mockPersonaManager.setPersona("creative")).thenReturn(true);
        when(mockPersonaManager.getCurrentPersonaCapitalized()).thenReturn("Creative");
        
        // Then
        assertThat(helpResult).isTrue();
        assertThat(personaResult).isTrue();
        
        verify(mockCLI).showHelp();
        verify(mockPersonaManager).setPersona("creative");
        verify(mockCLI).showPersonaChanged("Creative");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"/exit", "/quit", "/EXIT", "/QUIT", "/Exit", "/Quit"})
    @DisplayName("Should correctly identify exit commands")
    void shouldCorrectlyIdentifyExitCommands(String exitCommand) {
        // When
        boolean isExit = commandProcessor.isExitCommand(exitCommand);
        
        // Then
        assertThat(isExit).isTrue();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"/help", "/clear", "/persona", "exit", "quit", "hello"})
    @DisplayName("Should correctly identify non-exit commands")
    void shouldCorrectlyIdentifyNonExitCommands(String nonExitCommand) {
        // When
        boolean isExit = commandProcessor.isExitCommand(nonExitCommand);
        
        // Then
        assertThat(isExit).isFalse();
    }
    
    @Test
    @DisplayName("Should handle command with special characters")
    void shouldHandleCommandWithSpecialCharacters() {
        // When
        boolean result = commandProcessor.processCommand("/persona creative@123");
        
        // Given
        when(mockPersonaManager.setPersona("creative@123")).thenReturn(false);
        
        // Then
        assertThat(result).isTrue();
        verify(mockPersonaManager).setPersona("creative@123");
        verify(mockCLI).showError(contains("tidak ditemukan"));
    }
    
    @Test
    @DisplayName("Should maintain command processing state correctly")
    void shouldMaintainCommandProcessingStateCorrectly() {
        // Process multiple commands in sequence
        
        // Help command
        boolean result1 = commandProcessor.processCommand("/help");
        assertThat(result1).isTrue();
        
        // Clear command
        boolean result2 = commandProcessor.processCommand("/clear");
        assertThat(result2).isTrue();
        
        // Persona command
        when(mockPersonaManager.setPersona("teacher")).thenReturn(true);
        when(mockPersonaManager.getCurrentPersonaCapitalized()).thenReturn("Teacher");
        boolean result3 = commandProcessor.processCommand("/persona teacher");
        assertThat(result3).isTrue();
        
        // Exit command
        boolean result4 = commandProcessor.processCommand("/exit");
        assertThat(result4).isFalse();
        
        // Verify all interactions
        verify(mockCLI).showHelp();
        verify(mockCLI).clearScreen();
        verify(mockCLI).showWelcome();
        verify(mockPersonaManager).setPersona("teacher");
        verify(mockCLI).showPersonaChanged("Teacher");
    }
}
