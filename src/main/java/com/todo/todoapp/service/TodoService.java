package com.todo.todoapp.service;

import com.todo.todoapp.Todo;
import com.todo.todoapp.TodoRepository;
import com.todo.todoapp.User;
import com.todo.todoapp.UserRepository;
import com.todo.todoapp.dto.CreateTodoRequest;
import com.todo.todoapp.dto.TodoResponseDto;
import com.todo.todoapp.dto.UpdateTodoRequest;
import com.todo.todoapp.exception.TodoNotFoundException;
import com.todo.todoapp.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public List<TodoResponseDto> getAllTodos() {
        return todoRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TodoResponseDto> getTodosByUsername(String username) {
        User user = getUserByUsername(username);

        return todoRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public TodoResponseDto createTodo(String username, CreateTodoRequest request) {
        User user = getUserByUsername(username);

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDueDate(request.getDueDate());
        todo.setUser(user);
        todo.setPriority(request.getPriority() != null ? request.getPriority() : 1);

        Todo saved = todoRepository.save(todo);
        return mapToDto(saved);
    }

    public TodoResponseDto markAsCompleted(Long id, String username) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        if (!todo.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Brak dostępu do tego zadania");
        }

        todo.setCompleted(!Boolean.TRUE.equals(todo.getCompleted()));
        Todo saved = todoRepository.save(todo);
        return mapToDto(saved);
    }

    public void deleteTodo(Long id, String username) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        if (!todo.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Brak dostępu do tego zadania");
        }

        todoRepository.delete(todo);
    }

    public TodoResponseDto updateTodo(Long id, String username, UpdateTodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        if (!todo.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Brak dostępu do tego zadania");
        }

        todo.setTitle(request.getTitle());
        todo.setDueDate(request.getDueDate());
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }

        Todo saved = todoRepository.save(todo);
        return mapToDto(saved);
    }

    public List<TodoResponseDto> getCompletedTodosByUsername(String username) {
        User user = getUserByUsername(username);

        return todoRepository.findByUserIdAndCompleted(user.getId(), true)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TodoResponseDto> getPendingTodosByUsername(String username) {
        User user = getUserByUsername(username);

        return todoRepository.findByUserIdAndCompleted(user.getId(), false)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TodoResponseDto> getTodosSortedByDueDateByUsername(String username) {
        User user = getUserByUsername(username);

        return todoRepository.findByUserIdOrderByDueDateAsc(user.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TodoResponseDto> searchTodosByUsername(String username, String title) {
        User user = getUserByUsername(username);

        return todoRepository.findByUserIdAndTitleContainingIgnoreCase(user.getId(), title)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public Page<TodoResponseDto> getTodosPageByUsername(String username, int page, int size, String sortBy, String direction) {
        User user = getUserByUsername(username);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return todoRepository.findByUserId(user.getId(), pageable)
                .map(this::mapToDto);
    }

    private TodoResponseDto mapToDto(Todo todo) {
        return new TodoResponseDto(
                todo.getId(),
                todo.getTitle(),
                todo.getCompleted(),
                todo.getDueDate(),
                todo.getUser().getUsername(),
                todo.getPriority() != null ? todo.getPriority() : 1
        );
    }
}