package com.todo.todoapp.controller;


import com.todo.todoapp.User;
import com.todo.todoapp.UserController;
import com.todo.todoapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.List;

import static org.mockito.Mockito.when;
import com.todo.todoapp.exception.UsernameAlreadyExistsException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldGetAllUsers() throws Exception {
        User user1 = new User();
        user1.setUsername("julia");

        User user2 = new User();
        user2.setUsername("abc");

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("julia"))
                .andExpect(jsonPath("$[1].username").value("abc"));
    }

    @Test
    void shouldCreateUser() throws Exception {
        User user = new User();
        user.setUsername("testuser");

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "username": "testuser",
                                "password": "test123"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldRejectUserWithEmptyPassword() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "username": "testuser",
                                "password": ""
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDuplicateUsername() throws Exception {
        when(userService.createUser(any(User.class)))
                .thenThrow(new UsernameAlreadyExistsException("julia"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "username": "julia",
                                "password": "test123"
                            }
                            """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Username 'julia' jest już zajęty"));
    }
}
