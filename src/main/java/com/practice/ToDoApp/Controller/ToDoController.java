package com.practice.ToDoApp.Controller;

import com.practice.ToDoApp.DTO.ToDoRequestDTO;
import com.practice.ToDoApp.DTO.ToDoResponseDTO;
import com.practice.ToDoApp.Service.ToDoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
@Validated
public class ToDoController {

    private final ToDoService toDoService;

    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @PostMapping
    public ResponseEntity<ToDoResponseDTO> createTodo(
            @Valid @RequestBody ToDoRequestDTO request) {

        ToDoResponseDTO response = toDoService.createToDoList(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ToDoResponseDTO>> getTodosByUserId(
            @PathVariable
            @Positive(message = "User ID must be greater than 0")
            Integer userId) {

        List<ToDoResponseDTO> response =
                toDoService.getByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ToDoResponseDTO> updateTodo(
            @PathVariable
            @Positive(message = "User ID must be greater than 0")
            Integer userId,

            @Valid @RequestBody ToDoRequestDTO request) {

        ToDoResponseDTO response =
                toDoService.updateTodo(userId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteCompletedTodos(
            @PathVariable
            @Positive(message = "User ID must be greater than 0")
            Integer userId) {

        toDoService.deleteByUserId(userId);

        return ResponseEntity.noContent().build();
    }
}

