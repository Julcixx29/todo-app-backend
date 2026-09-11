package com.todo.todoapp.dto;

public class LoginResponse {
    private Long id;
    private String username;
    private String token;

    public LoginResponse(Long id, String username, String token) {
        this.id = id;
        this.username = username;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }
}
