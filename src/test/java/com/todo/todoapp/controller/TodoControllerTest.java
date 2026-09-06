package com.todo.todoapp.controller;

import com.todo.todoapp.TodoController;
import com.todo.todoapp.dto.TodoResponseDto;
import com.todo.todoapp.dto.UpdateTodoRequest;
import com.todo.todoapp.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.todo.todoapp.dto.CreateTodoRequest;
import com.todo.todoapp.dto.UpdateTodoRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import java.time.LocalDateTime;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Test
    void shouldGetTodosByUser() throws Exception {

        Long userId = 1L;

        TodoResponseDto todo = new TodoResponseDto(
                1L,
                "Nauczyć się Spring Boot",
                false,
                LocalDateTime.of(2026, 9, 10, 18, 0),
                "julcix"
        );

        when(todoService.getTodosByUser(userId))
                .thenReturn(List.of(todo));

        mockMvc.perform(get("/todos/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Nauczyć się Spring Boot"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[0].username").value("julcix"));
    }

    @Test
    void shouldCreateTodo() throws Exception {

        Long userId = 1L;

        TodoResponseDto response = new TodoResponseDto(
                1L,
                "Nauczyć się Spring Boot",
                false,
                LocalDateTime.of(2026, 9, 10, 18, 0),
                "julcix"
        );

        when(todoService.createTodo(eq(userId), any(CreateTodoRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/todos/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "Nauczyć się Spring Boot",
                                "dueDate": "2026-09-10T18:00:00"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Nauczyć się Spring Boot"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.username").value("julcix"));
    }

    @Test
    void shouldRejectTodoWithoutTitle() throws Exception {

        mockMvc.perform(post("/todos/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "",
                                "dueDate": "2026-09-10T18:00:00"
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateTodo() throws Exception {

        Long todoId = 1L;

        TodoResponseDto response = new TodoResponseDto(
                1L,
                "Nauczyć się Spring Boot - zaktualizowane",
                false,
                LocalDateTime.of(2026, 9, 12, 18, 0),
                "julcix"
        );

        when(todoService.updateTodo(eq(todoId), any(UpdateTodoRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "Nauczyć się Spring Boot - zaktualizowane",
                                "dueDate": "2026-09-12T18:00:00"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Nauczyć się Spring Boot - zaktualizowane"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.username").value("julcix"));
    }

    @Test
    void shouldMarkTodoAsCompleted() throws Exception {

        Long todoId = 1L;

        TodoResponseDto response = new TodoResponseDto(
                1L,
                "Nauczyć się Spring Boot",
                true,
                LocalDateTime.of(2026, 9, 10, 18, 0),
                "julcix"
        );

        when(todoService.markAsCompleted(todoId))
                .thenReturn(response);

        mockMvc.perform(patch("/todos/1/complete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Nauczyć się Spring Boot"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.username").value("julcix"));
    }

    @Test
    void shouldDeleteTodo() throws Exception {

        mockMvc.perform(delete("/todos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetCompletedTodos() throws Exception {

        Long userId = 1L;

        TodoResponseDto todo = new TodoResponseDto(
                1L,
                "Zrobione zadanie",
                true,
                LocalDateTime.of(2026, 9, 10, 18, 0),
                "julcix"
        );

        when(todoService.getCompletedTodos(userId))
                .thenReturn(List.of(todo));

        mockMvc.perform(get("/todos/user/1/completed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Zrobione zadanie"))
                .andExpect(jsonPath("$[0].completed").value(true))
                .andExpect(jsonPath("$[0].username").value("julcix"));
    }

    @Test
    void shouldGetPendingTodos() throws Exception {

        Long userId = 1L;

        TodoResponseDto todo = new TodoResponseDto(
                2L,
                "Zadanie do zrobienia",
                false,
                LocalDateTime.of(2026, 9, 11, 18, 0),
                "julcix"
        );

        when(todoService.getPendingTodos(userId))
                .thenReturn(List.of(todo));

        mockMvc.perform(get("/todos/user/1/pending"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].title").value("Zadanie do zrobienia"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[0].username").value("julcix"));
    }

    @Test
    void shouldGetSortedTodos() throws Exception {

        Long userId = 1L;

        TodoResponseDto todo = new TodoResponseDto(
                1L,
                "Najbliższe zadanie",
                false,
                LocalDateTime.of(2026, 9, 10, 18, 0),
                "julcix"
        );

        when(todoService.getTodosSortedByDueDate(userId))
                .thenReturn(List.of(todo));

        mockMvc.perform(get("/todos/user/1/sorted"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Najbliższe zadanie"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[0].username").value("julcix"));
    }

    @Test
    void shouldSearchTodos() throws Exception {

        Long userId = 1L;

        TodoResponseDto todo = new TodoResponseDto(
                1L,
                "Projekt Spring Boot",
                false,
                LocalDateTime.of(2026, 9, 10, 18, 0),
                "julcix"
        );

        when(todoService.searchTodos(userId, "projekt"))
                .thenReturn(List.of(todo));

        mockMvc.perform(get("/todos/user/1/search")
                        .param("title", "projekt"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Projekt Spring Boot"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[0].username").value("julcix"));
    }

    @Test
    void shouldGetTodosPage() throws Exception {

        TodoResponseDto todo = new TodoResponseDto(
                1L,
                "Zadanie ze strony",
                false,
                LocalDateTime.of(2026, 9, 10, 18, 0),
                "julcix"
        );

        Page<TodoResponseDto> page = new PageImpl<>(
                List.of(todo)
        );

        when(todoService.getTodosPage(
                eq(1L),
                eq(0),
                eq(5),
                eq("dueDate"),
                eq("asc")
        )).thenReturn(page);

        mockMvc.perform(get("/todos/user/1/page")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "dueDate")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Zadanie ze strony"))
                .andExpect(jsonPath("$.content[0].completed").value(false))
                .andExpect(jsonPath("$.content[0].username").value("julcix"));
    }
}