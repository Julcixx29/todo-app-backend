package com.todo.todoapp.dto;

import java.time.LocalDateTime;

public class TodoResponseDto {

    private Long id;
    private String title;
    private Boolean completed;
    private LocalDateTime dueDate;
    private String username;

    public TodoResponseDto(Long id, String title, Boolean completed, LocalDateTime dueDate, String username) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.dueDate = dueDate;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public String getUsername() {
        return username;
    }
}
