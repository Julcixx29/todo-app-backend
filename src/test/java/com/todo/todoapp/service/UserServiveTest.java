package com.todo.todoapp.service;
import com.todo.todoapp.dto.LoginRequest;
import com.todo.todoapp.dto.LoginResponse;
import com.todo.todoapp.User;
import com.todo.todoapp.UserRepository;
import com.todo.todoapp.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import com.todo.todoapp.exception.UsernameAlreadyExistsException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiveTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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

        when(passwordEncoder.encode("test123"))
                .thenReturn("zahaszowaneHaslo");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("testuser", result.getUsername());
        assertEquals("zahaszowaneHaslo", result.getPassword());

        verify(passwordEncoder).encode("test123");
        verify(userRepository).save(user);
    }

    @Test
    void shouldRejectDuplicateUsername() {
        User user = new User();
        user.setUsername("julia");
        user.setPassword("test123");

        when(userRepository.existsByUsername("julia")).thenReturn(true);

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userService.createUser(user)
        );

        verify(userRepository).existsByUsername("julia");
        verify(userRepository, never()).save(user);
    }

    @Test
    void shouldLoginUser() {
        User user = new User();
        user.setUsername("julia");
        user.setPassword("zahaszowaneHaslo");

        LoginRequest request = new LoginRequest();
        request.setUsername("julia");
        request.setPassword("test123");

        when(userRepository.findByUsername("julia"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches("test123", "zahaszowaneHaslo"))
                .thenReturn(true);

        LoginResponse result = userService.login(request);

        assertEquals("julia", result.getUsername());

        verify(passwordEncoder).matches("test123", "zahaszowaneHaslo");
    }

    @Test
    void shouldRejectInvalidPassword() {
        User user = new User();
        user.setUsername("julia");
        user.setPassword("zahaszowaneHaslo");

        LoginRequest request = new LoginRequest();
        request.setUsername("julia");
        request.setPassword("zlehaslo");

        when(userRepository.findByUsername("julia"))
                .thenReturn(java.util.Optional.of(user));

        when(passwordEncoder.matches("zlehaslo", "zahaszowaneHaslo"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );
    }
}
