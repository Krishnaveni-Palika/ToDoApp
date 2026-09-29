package com.practice.ToDoApp.Controller;

import com.practice.ToDoApp.DTO.ToDoRequestDTO;
import com.practice.ToDoApp.DTO.ToDoResponseDTO;
import com.practice.ToDoApp.Entity.ToDo;
import com.practice.ToDoApp.Service.ToDoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/todos")
public class ToDoController {

    @Autowired
    private ToDoService toDoService;
    @PostMapping
    public ResponseEntity<ToDoResponseDTO> createTodo(
            @Valid @RequestBody ToDoRequestDTO request) {

        ToDoResponseDTO response = toDoService.createToDoList(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{userId}")
    public List<ToDo> getToDoListById(@PathVariable Integer userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(" userId is mandatory");
        }
        return toDoService.getByUserId(userId);
    }

   @PatchMapping("/{userId}")
    public ToDoResponseDTO updateToDoList(@PathVariable Integer userId, @Valid @RequestBody ToDoRequestDTO toDoDto) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    " ID must be greater than 0"
            );
        }
        return toDoService.updateTodo(userId,toDoDto);
    }


    @DeleteMapping("/{userId}")
    public  void deleteToDoList(@PathVariable Integer userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    " ID must be greater than 0"
            );
        }
        toDoService.deleteByUserId(userId);
    }
}


