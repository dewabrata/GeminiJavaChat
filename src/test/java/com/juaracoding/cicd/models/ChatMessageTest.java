package com.juaracoding.cicd.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ChatMessage Tests")
class ChatMessageTest {

    @Test
    @DisplayName("Should create ChatMessage with valid role and content")
    void shouldCreateChatMessageWithValidRoleAndContent() {
        // Given
        String role = "user";
        String content = "Hello, how are you?";
        
        // When
        ChatMessage chatMessage = new ChatMessage(role, content);
        
        // Then
        assertThat(chatMessage.getRole()).isEqualTo(role);
        assertThat(chatMessage.getContent()).isEqualTo(content);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"user", "assistant", "system"})
    @DisplayName("Should handle different valid roles")
    void shouldHandleDifferentValidRoles(String role) {
        // Given
        String content = "Test content";
        
        // When
        ChatMessage chatMessage = new ChatMessage(role, content);
        
        // Then
        assertThat(chatMessage.getRole()).isEqualTo(role);
        assertThat(chatMessage.getContent()).isEqualTo(content);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should handle null and empty role")
    void shouldHandleNullAndEmptyRole(String role) {
        // Given
        String content = "Test content";
        
        // When
        ChatMessage chatMessage = new ChatMessage(role, content);
        
        // Then
        assertThat(chatMessage.getRole()).isEqualTo(role);
        assertThat(chatMessage.getContent()).isEqualTo(content);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should handle null and empty content")
    void shouldHandleNullAndEmptyContent(String content) {
        // Given
        String role = "user";
        
        // When
        ChatMessage chatMessage = new ChatMessage(role, content);
        
        // Then
        assertThat(chatMessage.getRole()).isEqualTo(role);
        assertThat(chatMessage.getContent()).isEqualTo(content);
    }
    
    @Test
    @DisplayName("Should allow modification of role via setter")
    void shouldAllowModificationOfRoleViaSetter() {
        // Given
        ChatMessage chatMessage = new ChatMessage("user", "content");
        String newRole = "assistant";
        
        // When
        chatMessage.setRole(newRole);
        
        // Then
        assertThat(chatMessage.getRole()).isEqualTo(newRole);
    }
    
    @Test
    @DisplayName("Should allow modification of content via setter")
    void shouldAllowModificationOfContentViaSetter() {
        // Given
        ChatMessage chatMessage = new ChatMessage("user", "old content");
        String newContent = "new content";
        
        // When
        chatMessage.setContent(newContent);
        
        // Then
        assertThat(chatMessage.getContent()).isEqualTo(newContent);
    }
    
    @Test
    @DisplayName("Should handle special characters in content")
    void shouldHandleSpecialCharactersInContent() {
        // Given
        String role = "user";
        String content = "Hello! 🤖 How are you? Special chars: @#$%^&*()";
        
        // When
        ChatMessage chatMessage = new ChatMessage(role, content);
        
        // Then
        assertThat(chatMessage.getRole()).isEqualTo(role);
        assertThat(chatMessage.getContent()).isEqualTo(content);
    }
    
    @Test
    @DisplayName("Should handle multiline content")
    void shouldHandleMultilineContent() {
        // Given
        String role = "assistant";
        String content = "Line 1\nLine 2\nLine 3";
        
        // When
        ChatMessage chatMessage = new ChatMessage(role, content);
        
        // Then
        assertThat(chatMessage.getRole()).isEqualTo(role);
        assertThat(chatMessage.getContent()).isEqualTo(content);
        assertThat(chatMessage.getContent()).contains("\n");
    }
    
    @Test
    @DisplayName("Should handle very long content")
    void shouldHandleVeryLongContent() {
        // Given
        String role = "user";
        String content = "a".repeat(10000); // Very long string
        
        // When
        ChatMessage chatMessage = new ChatMessage(role, content);
        
        // Then
        assertThat(chatMessage.getRole()).isEqualTo(role);
        assertThat(chatMessage.getContent()).hasSize(10000);
    }
}
