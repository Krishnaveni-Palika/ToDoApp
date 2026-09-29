package com.practice.ToDoApp.Service;

import com.practice.ToDoApp.DTO.ToDoRequestDTO;
import com.practice.ToDoApp.DTO.ToDoResponseDTO;
import com.practice.ToDoApp.Entity.ToDo;
import com.practice.ToDoApp.Exception.BadRequestException;
import com.practice.ToDoApp.Exception.ResourceNotFoundException;
import com.practice.ToDoApp.Mapper.ToDoMapper;
import com.practice.ToDoApp.Repository.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class ToDoService {
    @Autowired
    ToDoRepository toDoRepository;
    @Autowired
    ToDoMapper todoMapper;

    // CREATE TODOLIST

    public ToDoResponseDTO createToDoList(ToDoRequestDTO toDoRequestDto) {
        if (toDoRequestDto == null) {
            throw new BadRequestException("Todo request is required");
        }

        List<ToDo> existingTodos =
                toDoRepository.findByUserId(toDoRequestDto.getUserId());

        if (!existingTodos.isEmpty()) {

            String existingUserName =
                    existingTodos.get(0).getUserName();

            if (!existingUserName.equals(toDoRequestDto.getUserName())) {
                throw new BadRequestException(
                        "User name cannot be changed for user ID "
                                + toDoRequestDto.getUserId()
                );
            }
        }

        boolean taskExists = existingTodos.stream()
                .anyMatch(todo ->
                        todo.getTaskNumber()
                                .equals(toDoRequestDto.getTaskNumber())
                );

        if (taskExists) {
            throw new BadRequestException(
                    "Task number " + toDoRequestDto.getTaskNumber()
                            + " already exists for user "
                            + toDoRequestDto.getUserId()
            );
        }

        return todoMapper.createToDo(toDoRequestDto);
    }


    // GET BY USERID

    public List<ToDo> getByUserId(Integer userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        List<ToDo> todos = toDoRepository.findByUserId(userId);

        if (todos.isEmpty()) {
            throw new RuntimeException("User does not exist");
        }

        return todos;

    }

    // UPDATE TODOLIST

    public ToDoResponseDTO updateTodo(Integer userId,
                                      ToDoRequestDTO request) {
        if (Objects.isNull(request)) {
            throw new IllegalArgumentException("ToDo cannot be null");
        }
        List<ToDo> todos = toDoRepository.findByUserId(userId);

        ToDo existingTodo = todos.stream()
                .filter(todo ->
                        todo.getTaskNumber().equals(request.getTaskNumber()))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task " + request.getTaskNumber() +
                                        " not found for user " + userId
                        ));

        if (!existingTodo.getUserName().equalsIgnoreCase(request.getUserName())) {
            throw new BadRequestException(
                    "User name cannot be changed for user ID " + userId
            );
        }

        existingTodo.setDescription(request.getDescription());
        existingTodo.setDueDate(request.getDueDate());
        existingTodo.setStatus(request.getStatus());

        ToDo updatedTodo = toDoRepository.save(existingTodo);

        return new ToDoResponseDTO(
                updatedTodo.getUserId(),
                updatedTodo.getUserName(),
                updatedTodo.getTaskNumber(),
                updatedTodo.getDescription(),
                updatedTodo.getDueDate(),
                updatedTodo.getStatus()
        );
    }

    //DELETE BY USERID

    public void deleteByUserId(Integer userId) {

        if (userId == null) {
            throw new BadRequestException("User ID is required");
        }

        List<ToDo> todoList = toDoRepository.findByUserId(userId);

        if (todoList.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Todo not found for user " + userId);
        }

        List<ToDo> completedTodos = todoList.stream()
                .filter(todo ->
                        "COMPLETED".equalsIgnoreCase(todo.getStatus()))
                .toList();

        if (completedTodos.isEmpty()) {
            throw new BadRequestException(
                    "No completed todos found for user " + userId);
        }

        toDoRepository.deleteAll(completedTodos);
    }
}



