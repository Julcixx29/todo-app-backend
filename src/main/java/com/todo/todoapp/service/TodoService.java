package com.todo.todoapp.service;
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

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public List<Todo> getTodosByUser(Long userId) {
        return todoRepository.findByUserId(userId);
    }

    public Todo createTodo(Long userId, Todo todo) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        todo.setUser(user);
        return todoRepository.save(todo);
    }

    public Todo markAsCompleted(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));

        todo.setCompleted(true);
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }
}
