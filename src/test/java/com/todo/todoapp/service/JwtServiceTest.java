package com.todo.todoapp.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(1L, "julia");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldValidateCorrectToken() {
        String token = jwtService.generateToken(1L, "julia");

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertFalse(jwtService.isTokenValid("to-nie-jest-token"));
    }

    @Test
    void shouldGetUsernameFromToken() {
        String token = jwtService.generateToken(1L, "julia");

        String username = jwtService.getUsernameFromToken(token);

        assertEquals("julia", username);
    }

    @Test
    void shouldGetUserIdFromToken() {
        String token = jwtService.generateToken(1L, "julia");

        Long userId = jwtService.getUserIdFromToken(token);

        assertEquals(1L, userId);
    }
}
