package com.todo.todoapp.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Nieprawidłowy username lub hasło");
    }
}
