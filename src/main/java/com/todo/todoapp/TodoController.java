package com.todo.todoapp;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoController(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    @PostMapping("/user/{userId}")
    public Todo createTodo(@PathVariable Long userId, @RequestBody Todo todo) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        todo.setUser(user);
        return todoRepository.save(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo updateTodo) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setTitle(updateTodo.getTitle());
        return todoRepository.save(updateTodo);
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
