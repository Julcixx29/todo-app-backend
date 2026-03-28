package com.todo.todoapp;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        return todoRepository.save(todo);
    }

    @PatchMapping("/{id}/complete")
    public Todo markAsCompleted(@PathVariable Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new RuntimeException("Złe id"));
        todo.setCompleted(true);
        return todoRepository.save(todo);
    }

    @GetMapping("/user/{userId}")
    public List<Todo> getTodosByUser(@PathVariable Long userId) {
        return todoRepository.findByUserId(userId);
    }
}
