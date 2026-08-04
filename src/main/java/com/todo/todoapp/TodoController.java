package com.todo.todoapp;
import com.todo.todoapp.dto.CreateTodoRequest;
import com.todo.todoapp.dto.TodoResponseDto;
import com.todo.todoapp.dto.UpdateTodoRequest;
import com.todo.todoapp.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponseDto> getAllTodos() {
        return todoService.getAllTodos();
    }

    @GetMapping("/user/{userId}")
    public List<TodoResponseDto> getTodosByUser(@PathVariable Long userId) {
        return todoService.getTodosByUser(userId);
    }

    @PostMapping("/user/{userId}")
    public TodoResponseDto createTodo(@PathVariable Long userId, @Valid @RequestBody CreateTodoRequest request) {
        return todoService.createTodo(userId, request);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }

    @PutMapping("/{id}")
    public TodoResponseDto updateTodo(@PathVariable Long id, @Valid @RequestBody UpdateTodoRequest request) {
        return todoService.updateTodo(id, request);
    }

    @PatchMapping("/{id}/complete")
    public TodoResponseDto markAsCompleted(@PathVariable Long id) {
        return todoService.markAsCompleted(id);
    }

    @GetMapping("/user/{userId}/completed")
    public List<TodoResponseDto> getCompletedTodos(@PathVariable Long userId) {
        return todoService.getCompletedTodos(userId);
    }

    @GetMapping("/user/{userId}/pending")
    public List<TodoResponseDto> getPendingTodos(@PathVariable Long userId) {
        return todoService.getPendingTodos(userId);
    }

    @GetMapping("/user/{userId}/sorted")
    public List<TodoResponseDto> getSortedTodos(@PathVariable Long userId) {
        return todoService.getTodosSortedByDueDate(userId);
    }

    @GetMapping("/user/{userId}/search")
    public List<TodoResponseDto> searchTodos(@PathVariable Long userId, @RequestParam String title) {
        return todoService.searchTodos(userId, title);
    }

    @GetMapping("/user/{userId}/page")
    public Page<TodoResponseDto> getTodosPage(@PathVariable Long userId, @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "5") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "dueDate") String sortBy, @RequestParam(defaultValue = "asc") String direction) {
        return todoService.getTodosPage(userId, page, size, sortBy, direction);
    }
}
