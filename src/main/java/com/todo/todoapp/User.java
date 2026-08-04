package com.todo.todoapp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username nie może być pusty")
    @Size(min = 3, max = 20, message = "Username musi mieć o 3 do 20 znaków")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Hasło nie może być puste")
    @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Todo> todos;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }
}
