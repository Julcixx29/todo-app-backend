package com.todo.todoapp.service;

import com.todo.todoapp.User;
import com.todo.todoapp.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiveTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldGetAllUsers() {
        User user1 = new User();
        user1.setUsername("julia");

        User user2 = new User();
        user2.setUsername("abc");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("julia", result.get(0).getUsername());
        assertEquals("abc", result.get(1).getUsername());

        verify(userRepository).findAll();
    }

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("test123");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("testuser", result.getUsername());
        assertEquals("test123", result.getPassword());

        verify(userRepository).save(user);
    }
}
