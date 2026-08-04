package com.todo.todoapp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserId(Long userID);
    List<Todo>findByUserIdAndCompleted(Long userID, Boolean completed);
    List<Todo>findByUserIdOrderByDueDateAsc(Long userID);
    List<Todo>findByUserIdAndTitleContainingIgnoreCase(Long userID, String title);
    Page<Todo>findByUserId(Long userID, Pageable pageable);
}
