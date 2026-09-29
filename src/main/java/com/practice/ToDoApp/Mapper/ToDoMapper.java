package com.practice.ToDoApp.Mapper;

import com.practice.ToDoApp.DTO.ToDoRequestDTO;
import com.practice.ToDoApp.DTO.ToDoResponseDTO;
import com.practice.ToDoApp.Entity.ToDo;
import com.practice.ToDoApp.Exception.BadRequestException;
import com.practice.ToDoApp.Repository.ToDoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class ToDoMapper {

    private final ToDoRepository toDoRepository;

    public ToDoMapper(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    public ToDoResponseDTO createToDo(ToDoRequestDTO req) {

        // Map Request DTO to Entity
        ToDo todo = new ToDo();

        todo.setUserId(req.getUserId());
        todo.setUserName(req.getUserName());
        todo.setTaskNumber(req.getTaskNumber());
        todo.setDescription(req.getDescription());
        todo.setDueDate(req.getDueDate());
        todo.setStatus(req.getStatus());

        // Save Entity
        ToDo savedTodo = toDoRepository.save(todo);

        // Map Entity to Response DTO
        return new ToDoResponseDTO(
                savedTodo.getUserId(),
                savedTodo.getUserName(),
                savedTodo.getTaskNumber(),
                savedTodo.getDescription(),
                savedTodo.getDueDate(),
                savedTodo.getStatus()
        );
    }
}