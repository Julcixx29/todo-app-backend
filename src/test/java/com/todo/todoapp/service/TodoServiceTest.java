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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void shouldGetAllTodos() {
        User user = new User();
        user.setUsername("julia");

        Todo todo = new Todo();
        todo.setTitle("Wszystkie zadania");
        todo.setCompleted(false);
        todo.setUser(user);

        when(todoRepository.findAll()).thenReturn(List.of(todo));

        List<TodoResponseDto> result = todoService.getAllTodos();

        assertEquals(1, result.size());
        assertEquals("Wszystkie zadania", result.get(0).getTitle());
    }

    @Test
    void shouldGetTodosByUsername() {
        User user = new User();
        user.setUsername("julia");

        Todo todo = new Todo();
        todo.setTitle("Moje zadanie");
        todo.setCompleted(false);
        todo.setUser(user);

        when(userRepository.findByUsername("julia")).thenReturn(Optional.of(user));
        when(todoRepository.findByUserId(user.getId())).thenReturn(List.of(todo));

        List<TodoResponseDto> result = todoService.getTodosByUsername("julia");

        assertEquals(1, result.size());
        assertEquals("Moje zadanie", result.get(0).getTitle());
    }

    @Test
    void shouldCreateTodo() {
        User user = new User();
        user.setUsername("julia");

        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Próba testu");
        request.setDueDate(LocalDateTime.of(2026, 9, 10, 18, 0));

        Todo savedTodo = new Todo();
        savedTodo.setTitle("Próba testu");
        savedTodo.setCompleted(false);
        savedTodo.setDueDate(request.getDueDate());
        savedTodo.setUser(user);

        when(userRepository.findByUsername("julia")).thenReturn(Optional.of(user));
        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        TodoResponseDto result = todoService.createTodo("julia", request);

        assertEquals("Próba testu", result.getTitle());
        assertEquals(false, result.getCompleted());
        assertEquals(request.getDueDate(), result.getDueDate());
        assertEquals("julia", result.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Nauczyć się testów");
        request.setDueDate(LocalDateTime.of(2026, 9, 10, 18, 0));

        when(userRepository.findByUsername("nieznany")).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> todoService.createTodo("nieznany", request)
        );
    }

    @Test
    void shouldMarkTodoAsCompleted() {
        Long todoId = 1L;

        User user = new User();
        user.setUsername("julia");

        Todo todo = new Todo();
        todo.setTitle("Nauczyć się testów");
        todo.setCompleted(false);
        todo.setDueDate(LocalDateTime.of(2026, 9, 10, 18, 0));
        todo.setUser(user);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(todoRepository.save(todo)).thenReturn(todo);

        TodoResponseDto result = todoService.markAsCompleted(todoId, "julia");

        assertTrue(result.getCompleted());
        assertEquals("Nauczyć się testów", result.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenOtherUserTriesToMarkAsCompleted() {
        Long todoId = 1L;

        User owner = new User();
        owner.setUsername("julia");

        Todo todo = new Todo();
        todo.setUser(owner);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> todoService.markAsCompleted(todoId, "innyUzytkownik")
        );

        assertEquals("Brak dostępu do tego zadania", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTodoDoesNotExist() {
        Long todoId = 999L;

        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        assertThrows(
                TodoNotFoundException.class,
                () -> todoService.markAsCompleted(todoId, "julia")
        );
    }

    @Test
    void shouldDeleteTodo() {
        Long todoId = 1L;

        User user = new User();
        user.setUsername("julia");

        Todo todo = new Todo();
        todo.setUser(user);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        todoService.deleteTodo(todoId, "julia");

        verify(todoRepository).delete(todo);
    }

    @Test
    void shouldThrowExceptionWhenOtherUserTriesToDeleteTodo() {
        Long todoId = 1L;

        User owner = new User();
        owner.setUsername("julia");

        Todo todo = new Todo();
        todo.setUser(owner);

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> todoService.deleteTodo(todoId, "innyUzytkownik")
        );

        assertEquals("Brak dostępu do tego zadania", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingTodoDoesNotExist() {
        Long todoId = 999L;

        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        assertThrows(
                TodoNotFoundException.class,
                () -> todoService.deleteTodo(todoId, "julia")
        );
    }

    @Test
    void shouldUpdateTodo() {
        Long todoId = 1L;

        User user = new User();
        user.setUsername("julia");

        Todo todo = new Todo();
        todo.setTitle("Stary tytuł");
        todo.setCompleted(false);
        todo.setDueDate(LocalDateTime.of(2026, 9, 10, 18, 0));
        todo.setUser(user);

        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Nowy tytuł");
        request.setDueDate(LocalDateTime.of(2026, 9, 15, 20, 0));

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(todoRepository.save(todo)).thenReturn(todo);

        TodoResponseDto result = todoService.updateTodo(todoId, "julia", request);

        assertEquals("Nowy tytuł", result.getTitle());
        assertEquals(LocalDateTime.of(2026, 9, 15, 20, 0), result.getDueDate());
    }

    @Test
    void shouldThrowExceptionWhenOtherUserTriesToUpdateTodo() {
        Long todoId = 1L;

        User owner = new User();
        owner.setUsername("julia");

        Todo todo = new Todo();
        todo.setUser(owner);

        UpdateTodoRequest request = new UpdateTodoRequest();

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> todoService.updateTodo(todoId, "innyUzytkownik", request)
        );

        assertEquals("Brak dostępu do tego zadania", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTodoDoesNotExist() {
        Long todoId = 999L;

        UpdateTodoRequest request = new UpdateTodoRequest();

        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        assertThrows(
                TodoNotFoundException.class,
                () -> todoService.updateTodo(todoId, "julia", request)
        );
    }

    @Test
    void shouldGetCompletedTodos() {
        User user = new User();
        user.setUsername("julia");

        Todo completedTodo = new Todo();
        completedTodo.setTitle("Ukończone zadanie");
        completedTodo.setCompleted(true);
        completedTodo.setUser(user);

        when(userRepository.findByUsername("julia")).thenReturn(Optional.of(user));
        when(todoRepository.findByUserIdAndCompleted(user.getId(), true)).thenReturn(List.of(completedTodo));

        List<TodoResponseDto> result = todoService.getCompletedTodosByUsername("julia");

        assertEquals(1, result.size());
        assertEquals("Ukończone zadanie", result.get(0).getTitle());
        assertTrue(result.get(0).getCompleted());
    }

    @Test
    void shouldGetPendingTodos() {
        User user = new User();
        user.setUsername("julia");

        Todo pendingTodo = new Todo();
        pendingTodo.setTitle("Zadanie do zrobienia");
        pendingTodo.setCompleted(false);
        pendingTodo.setUser(user);

        when(userRepository.findByUsername("julia")).thenReturn(Optional.of(user));
        when(todoRepository.findByUserIdAndCompleted(user.getId(), false)).thenReturn(List.of(pendingTodo));

        List<TodoResponseDto> result = todoService.getPendingTodosByUsername("julia");

        assertEquals(1, result.size());
        assertEquals("Zadanie do zrobienia", result.get(0).getTitle());
        assertFalse(result.get(0).getCompleted());
    }

    @Test
    void shouldGetTodosSortedByDueDate() {
        User user = new User();
        user.setUsername("julia");

        Todo firstTodo = new Todo();
        firstTodo.setTitle("Pierwsze zadanie");
        firstTodo.setCompleted(false);
        firstTodo.setDueDate(LocalDateTime.of(2026, 9, 10, 18, 0));
        firstTodo.setUser(user);

        Todo secondTodo = new Todo();
        secondTodo.setTitle("Drugie zadanie");
        secondTodo.setCompleted(false);
        secondTodo.setDueDate(LocalDateTime.of(2026, 9, 15, 18, 0));
        secondTodo.setUser(user);

        when(userRepository.findByUsername("julia")).thenReturn(Optional.of(user));
        when(todoRepository.findByUserIdOrderByDueDateAsc(user.getId())).thenReturn(List.of(firstTodo, secondTodo));

        List<TodoResponseDto> result = todoService.getTodosSortedByDueDateByUsername("julia");

        assertEquals(2, result.size());
        assertEquals("Pierwsze zadanie", result.get(0).getTitle());
        assertEquals("Drugie zadanie", result.get(1).getTitle());
    }

    @Test
    void shouldSearchTodosByTitle() {
        String title = "projekt";

        User user = new User();
        user.setUsername("julia");

        Todo todo = new Todo();
        todo.setTitle("Projekt Java");
        todo.setCompleted(false);
        todo.setUser(user);

        when(userRepository.findByUsername("julia")).thenReturn(Optional.of(user));
        when(todoRepository.findByUserIdAndTitleContainingIgnoreCase(user.getId(), title)).thenReturn(List.of(todo));

        List<TodoResponseDto> result = todoService.searchTodosByUsername("julia", title);

        assertEquals(1, result.size());
        assertEquals("Projekt Java", result.get(0).getTitle());
    }

    @Test
    void shouldGetTodosPage() {
        User user = new User();
        user.setUsername("julia");

        Todo todo = new Todo();
        todo.setTitle("Zadanie");
        todo.setCompleted(false);
        todo.setUser(user);

        Page<Todo> todoPage = new PageImpl<>(List.of(todo));

        when(userRepository.findByUsername("julia")).thenReturn(Optional.of(user));
        when(todoRepository.findByUserId(eq(user.getId()), any(Pageable.class))).thenReturn(todoPage);

        Page<TodoResponseDto> result = todoService.getTodosPageByUsername("julia", 0, 5, "dueDate", "asc");

        assertEquals(1, result.getContent().size());
        assertEquals("Zadanie", result.getContent().get(0).getTitle());
    }
}