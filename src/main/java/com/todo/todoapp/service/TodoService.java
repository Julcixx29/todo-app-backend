package com.todo.todoapp.service;
import com.todo.todoapp.dto.CreateTodoRequest;
import com.todo.todoapp.dto.TodoResponseDto;
import com.todo.todoapp.dto.UpdateTodoRequest;
import com.todo.todoapp.exception.UserNotFoundException;
import com.todo.todoapp.exception.TodoNotFoundException;
import com.todo.todoapp.*;
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

    public List<TodoResponseDto> getCompletedTodos(Long userId) {
        return todoRepository.findByUserIdAndCompleted(userId, true)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TodoResponseDto> getPendingTodos(Long userId) {
       return todoRepository.findByUserIdAndCompleted(userId, false)
               .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TodoResponseDto> getTodosSortedByDueDate(Long userId) {
        return todoRepository.findByUserIdOrderByDueDateAsc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<TodoResponseDto> searchTodos(Long userId, String title) {
        return todoRepository.findByUserIdAndTitleContainingIgnoreCase(userId, title)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public Page<TodoResponseDto> getTodosPage(Long userId, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return todoRepository.findByUserId(userId, pageable)
                .map(this::mapToDto);
    }
}
