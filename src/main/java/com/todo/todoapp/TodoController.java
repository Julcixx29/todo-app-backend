package com.todo.todoapp;
import com.todo.todoapp.dto.CreateTodoRequest;
import com.todo.todoapp.dto.TodoResponseDto;
import com.todo.todoapp.dto.UpdateTodoRequest;
import com.todo.todoapp.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/user")
    public List<TodoResponseDto> getTodosByUser(Authentication authentication) {
        return todoService.getTodosByUsername(authentication.getName());
    }

    @PostMapping("/user")
    public TodoResponseDto createTodo(Authentication authentication, @Valid @RequestBody CreateTodoRequest request) {
        return todoService.createTodo(authentication.getName(), request);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id, Authentication authentication) {
        todoService.deleteTodo(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public TodoResponseDto updateTodo(@PathVariable Long id, Authentication authentication, @Valid @RequestBody UpdateTodoRequest request) {
        return todoService.updateTodo(
                id,
                authentication.getName(),
                request);
    }

    @PatchMapping("/{id}/complete")
    public TodoResponseDto markAsCompleted(@PathVariable Long id, Authentication authentication) {
        return todoService.markAsCompleted(
                id,
                authentication.getName()
        );
    }

    @GetMapping("/user/completed")
    public List<TodoResponseDto> getCompletedTodos(Authentication authentication) {
        return todoService.getCompletedTodosByUsername(authentication.getName());
    }

    @GetMapping("/user/pending")
    public List<TodoResponseDto> getPendingTodos(Authentication authentication) {
        return todoService.getPendingTodosByUsername(authentication.getName());
    }

    @GetMapping("/user/sorted")
    public List<TodoResponseDto> getSortedTodos(Authentication authentication) {
        return todoService.getTodosSortedByDueDateByUsername(authentication.getName());
    }

    @GetMapping("/user/search")
    public List<TodoResponseDto> searchTodos(Authentication authentication, @RequestParam String title) {
        return todoService.searchTodosByUsername(authentication.getName(), title);
    }

    @GetMapping("/user/page")
    public Page<TodoResponseDto> getTodosPage(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return todoService.getTodosPageByUsername(authentication.getName(), page, size, sortBy, direction);
    }
}
