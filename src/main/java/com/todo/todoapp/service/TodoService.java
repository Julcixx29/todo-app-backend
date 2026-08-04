package com.todo.todoapp.service;
import com.todo.todoapp.dto.CreateTodoRequest;
import com.todo.todoapp.dto.TodoResponseDto;
import com.todo.todoapp.dto.UpdateTodoRequest;
import com.todo.todoapp.exception.UserNotFoundException;
import com.todo.todoapp.exception.TodoNotFoundException;
import com.todo.todoapp.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public List<TodoResponseDto> getAllTodos() {
        return todoRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TodoResponseDto> getTodosByUser(Long userId) {
        return todoRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public TodoResponseDto createTodo(Long userId, CreateTodoRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDueDate(request.getDueDate());
        todo.setUser(user);

        Todo saved = todoRepository.save(todo);
        return mapToDto(saved);
    }

    public TodoResponseDto markAsCompleted(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));

        todo.setCompleted(true);
        Todo saved = todoRepository.save(todo);
        return mapToDto(saved);
    }

    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }

    private TodoResponseDto mapToDto(Todo todo) {
        return new TodoResponseDto(
                todo.getId(),
                todo.getTitle(),
                todo.getCompleted(),
                todo.getDueDate(),
                todo.getUser().getUsername()
        );
    }

    public TodoResponseDto updateTodo(Long id, UpdateTodoRequest request) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));

        todo.setTitle(request.getTitle());
        todo.setDueDate(request.getDueDate());

        Todo saved = todoRepository.save(todo);
        return mapToDto(saved);
    }
}
