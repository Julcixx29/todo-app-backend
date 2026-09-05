package com.todo.todoapp.service;
import com.todo.todoapp.Todo;
import com.todo.todoapp.User;
import com.todo.todoapp.TodoRepository;
import com.todo.todoapp.UserRepository;
import com.todo.todoapp.dto.CreateTodoRequest;
import com.todo.todoapp.dto.TodoResponseDto;
import com.todo.todoapp.dto.UpdateTodoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {
    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void shouldCreateTodo() {

        // GIVEN
        Long userId = 1L;

        User user = new User();
        user.setUsername("julia");

        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Próba testu");
        request.setDueDate(LocalDateTime.of(2026, 9, 10,18, 0));

        Todo savedTodo = new Todo();
        savedTodo.setTitle("Próba testu");
        savedTodo.setCompleted(false);
        savedTodo.setDueDate(request.getDueDate());
        savedTodo.setUser(user);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(todoRepository.save(org.mockito.ArgumentMatchers.any(Todo.class)))
                .thenReturn(savedTodo);

        // WHEN
        TodoResponseDto result = todoService.createTodo(userId, request);

        // THEN
        assertEquals("Próba testu", result.getTitle());
        assertEquals(false, result.getCompleted());
        assertEquals(request.getDueDate(), result.getDueDate());
        assertEquals("julia", result.getUsername());

    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        // GIVEN
        Long userId = 999L;

        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Nauczyć się testów");
        request.setDueDate(LocalDateTime.of(2026, 9, 10,18, 0));

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        //WHEN+THEN
        org.junit.jupiter.api.Assertions.assertThrows(
                com.todo.todoapp.exception.UserNotFoundException.class,
                () -> todoService.createTodo(userId, request));

    }

    @Test
    void shouldMarkTodoAsCompleted() {

        // GIVEN
        Long todoId = 1L;

        User user = new User();
        user.setUsername("julia");

        Todo todo = new Todo();
        todo.setTitle("Nauczyć się testów");
        todo.setCompleted(false);
        todo.setDueDate(LocalDateTime.of(2026, 9, 10, 18, 0));
        todo.setUser(user);

        when(todoRepository.findById(todoId))
                .thenReturn(Optional.of(todo));

        when(todoRepository.save(todo))
                .thenReturn(todo);

        // WHEN
        TodoResponseDto result = todoService.markAsCompleted(todoId);

        // THEN
        assertEquals(true, result.getCompleted());
        assertEquals("Nauczyć się testów", result.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenTodoDoesNotExist() {

        // GIVEN
        Long todoId = 999L;

        when(todoRepository.findById(todoId))
                .thenReturn(Optional.empty());

        // WHEN + THEN
        org.junit.jupiter.api.Assertions.assertThrows(
                com.todo.todoapp.exception.TodoNotFoundException.class,
                () -> todoService.markAsCompleted(todoId)
        );
    }

    @Test
    void shouldDeleteTodo() {

        // GIVEN
        Long todoId = 1L;

        when(todoRepository.existsById(todoId))
                .thenReturn(true);

        // WHEN
        todoService.deleteTodo(todoId);

        // THEN
        org.mockito.Mockito.verify(todoRepository)
                .deleteById(todoId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingTodoDoesNotExist() {

        // GIVEN
        Long todoId = 999L;

        when(todoRepository.existsById(todoId))
                .thenReturn(false);

        // WHEN + THEN
        org.junit.jupiter.api.Assertions.assertThrows(
                com.todo.todoapp.exception.TodoNotFoundException.class,
                () -> todoService.deleteTodo(todoId)
        );
    }

    @Test
    void shouldUpdateTodo() {

        // GIVEN
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

        when(todoRepository.findById(todoId))
                .thenReturn(Optional.of(todo));

        when(todoRepository.save(todo))
                .thenReturn(todo);

        // WHEN
        TodoResponseDto result = todoService.updateTodo(todoId, request);

        // THEN
        assertEquals("Nowy tytuł", result.getTitle());
        assertEquals(
                LocalDateTime.of(2026, 9, 15, 20, 0),
                result.getDueDate()
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTodoDoesNotExist() {

        // GIVEN
        Long todoId = 999L;

        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Nowy tytuł");
        request.setDueDate(LocalDateTime.of(2026, 9, 15, 20, 0));

        when(todoRepository.findById(todoId))
                .thenReturn(Optional.empty());

        // WHEN + THEN
        org.junit.jupiter.api.Assertions.assertThrows(
                com.todo.todoapp.exception.TodoNotFoundException.class,
                () -> todoService.updateTodo(todoId, request)
        );
    }

    @Test
    void shouldGetCompletedTodos() {

        // GIVEN
        Long userId = 1L;

        User user = new User();
        user.setUsername("julia");

        Todo completedTodo = new Todo();
        completedTodo.setTitle("Ukończone zadanie");
        completedTodo.setCompleted(true);
        completedTodo.setUser(user);

        when(todoRepository.findByUserIdAndCompleted(userId, true))
                .thenReturn(java.util.List.of(completedTodo));

        // WHEN
        java.util.List<TodoResponseDto> result =
                todoService.getCompletedTodos(userId);

        // THEN
        assertEquals(1, result.size());
        assertEquals("Ukończone zadanie", result.get(0).getTitle());
        assertEquals(true, result.get(0).getCompleted());
    }

    @Test
    void shouldGetPendingTodos() {

        // GIVEN
        Long userId = 1L;

        User user = new User();
        user.setUsername("julia");

        Todo pendingTodo = new Todo();
        pendingTodo.setTitle("Zadanie do zrobienia");
        pendingTodo.setCompleted(false);
        pendingTodo.setUser(user);

        when(todoRepository.findByUserIdAndCompleted(userId, false))
                .thenReturn(java.util.List.of(pendingTodo));

        // WHEN
        java.util.List<TodoResponseDto> result =
                todoService.getPendingTodos(userId);

        // THEN
        assertEquals(1, result.size());
        assertEquals("Zadanie do zrobienia", result.get(0).getTitle());
        assertEquals(false, result.get(0).getCompleted());
    }

    @Test
    void shouldGetTodosSortedByDueDate() {

        // GIVEN
        Long userId = 1L;

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

        when(todoRepository.findByUserIdOrderByDueDateAsc(userId))
                .thenReturn(java.util.List.of(firstTodo, secondTodo));

        // WHEN
        java.util.List<TodoResponseDto> result =
                todoService.getTodosSortedByDueDate(userId);

        // THEN
        assertEquals(2, result.size());
        assertEquals("Pierwsze zadanie", result.get(0).getTitle());
        assertEquals("Drugie zadanie", result.get(1).getTitle());
    }

    @Test
    void shouldSearchTodosByTitle() {

        // GIVEN
        Long userId = 1L;
        String title = "projekt";

        User user = new User();
        user.setUsername("julia");

        Todo todo = new Todo();
        todo.setTitle("Projekt Java");
        todo.setCompleted(false);
        todo.setUser(user);

        when(todoRepository.findByUserIdAndTitleContainingIgnoreCase(userId, title))
                .thenReturn(java.util.List.of(todo));

        // WHEN
        java.util.List<TodoResponseDto> result =
                todoService.searchTodos(userId, title);

        // THEN
        assertEquals(1, result.size());
        assertEquals("Projekt Java", result.get(0).getTitle());
    }

    @Test
    void shouldGetTodosPage() {

        // GIVEN
        Long userId = 1L;

        Todo todo = new Todo();
        todo.setTitle("Zadanie");
        todo.setCompleted(false);

        User user = new User();
        user.setUsername("julia");
        todo.setUser(user);

        org.springframework.data.domain.Page<Todo> todoPage =
                new org.springframework.data.domain.PageImpl<>(
                        java.util.List.of(todo)
                );

        when(todoRepository.findByUserId(
                org.mockito.ArgumentMatchers.eq(userId),
                org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Pageable.class)
        )).thenReturn(todoPage);

        // WHEN
        org.springframework.data.domain.Page<TodoResponseDto> result =
                todoService.getTodosPage(userId, 0, 5, "dueDate", "asc");

        // THEN
        assertEquals(1, result.getContent().size());
        assertEquals("Zadanie", result.getContent().get(0).getTitle());
    }
}
