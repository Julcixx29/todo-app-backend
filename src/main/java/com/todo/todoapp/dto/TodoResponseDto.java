package com.todo.todoapp.dto;

import java.time.LocalDateTime;

public class TodoResponseDto {
    private Long id;
    private String title;
    private Boolean completed;
    private LocalDateTime dueDate;
    private String username;
    private Integer priority;

    public TodoResponseDto(Long id, String title, Boolean completed, LocalDateTime dueDate, String username, Integer priority) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.dueDate = dueDate;
        this.username = username;
        this.priority = priority;
    }

    public TodoResponseDto(Long id, String title, Boolean completed, LocalDateTime dueDate, String username) {
        this(id, title, completed, dueDate, username, 1);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Boolean getCompleted() { return completed; }
    public LocalDateTime getDueDate() { return dueDate; }
    public String getUsername() { return username; }
    public Integer getPriority() { return priority; }
}