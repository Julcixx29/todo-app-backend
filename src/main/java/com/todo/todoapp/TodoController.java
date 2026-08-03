package com.todo.todoapp;
import com.todo.todoapp.dto.TodoResponseDto;
import com.todo.todoapp.service.TodoService;
import jakarta.validation.Valid;
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
    public TodoResponseDto createTodo(@PathVariable Long userId, @Valid @RequestBody Todo todo) {
        return todoService.createTodo(userId, todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }

//    @PutMapping("/{id}")
//    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo updateTodo) {
//        Todo todo = todoRepository.findById(id).orElseThrow(() -> new RuntimeException("Todo not found"));
//
//        todo.setTitle(updateTodo.getTitle());
//        return todoRepository.save(updateTodo);
//    }

    @PatchMapping("/{id}/complete")
    public TodoResponseDto markAsCompleted(@PathVariable Long id) {
        return todoService.markAsCompleted(id);
    }
}
