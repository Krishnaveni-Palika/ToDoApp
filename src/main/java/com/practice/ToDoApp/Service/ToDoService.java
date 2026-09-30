package com.practice.ToDoApp.Service;

import com.practice.ToDoApp.DTO.ToDoRequestDTO;
import com.practice.ToDoApp.DTO.ToDoResponseDTO;
import com.practice.ToDoApp.Entity.ToDo;
import com.practice.ToDoApp.Exception.BadRequestException;
import com.practice.ToDoApp.Exception.ResourceNotFoundException;
import com.practice.ToDoApp.Mapper.ToDoMapper;
import com.practice.ToDoApp.Repository.ToDoRepository;
import com.practice.ToDoApp.Utils.Constants;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;
    private final ToDoMapper todoMapper;

    public ToDoService(ToDoRepository toDoRepository,
                       ToDoMapper todoMapper) {
        this.toDoRepository = toDoRepository;
        this.todoMapper = todoMapper;
    }

    // CREATE TODOLIST

    public ToDoResponseDTO createToDoList(ToDoRequestDTO request) {

        if (request == null) {
            throw new BadRequestException("Todo request is required");
        }

        List<ToDo> existingTodos =
                toDoRepository.findByUserId(request.getUserId());

        // Check username for existing user
        if (!existingTodos.isEmpty()) {

            String existingUserName =
                    existingTodos.getFirst().getUserName();

            if (!existingUserName.equals(request.getUserName())) {
                throw new BadRequestException(
                        "User name cannot be changed for user ID "
                                + request.getUserId()
                );
            }
        }

        // Check duplicate task number
        boolean taskExists = existingTodos.stream()
                .anyMatch(todo ->
                        todo.getTaskNumber()
                                .equals(request.getTaskNumber())
                );

        if (taskExists) {
            throw new BadRequestException(
                    "Task number " + request.getTaskNumber()
                            + " already exists for user "
                            + request.getUserId()
            );
        }

        return todoMapper.createToDo(request);
    }


    // GET TODOS BY USER ID

    public List<ToDoResponseDTO> getByUserId(Integer userId) {

        if (userId == null) {
            throw new BadRequestException("User ID is required");
        }

        List<ToDo> todos =
                toDoRepository.findByUserId(userId);

        if (todos.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Todo not found for user " + userId
            );
        }

        return todos.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }


    // UPDATE TODOLIST

    public ToDoResponseDTO updateTodo(Integer userId,
                                      ToDoRequestDTO request) {

        if (userId == null) {
            throw new BadRequestException("User ID is required");
        }

        if (request == null) {
            throw new BadRequestException("Todo request is required");
        }

        List<ToDo> todos =
                toDoRepository.findByUserId(userId);

        ToDo existingTodo = todos.stream()
                .filter(todo ->
                        todo.getTaskNumber()
                                .equals(request.getTaskNumber()))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Task " + request.getTaskNumber()
                                        + " not found for user "
                                        + userId
                        )
                );

        // Username cannot be changed
        if (!existingTodo.getUserName()
                .equalsIgnoreCase(request.getUserName())) {

            throw new BadRequestException(
                    "User name cannot be changed for user ID "
                            + userId
            );
        }

        // Update allowed fields
        existingTodo.setDescription(request.getDescription());
        existingTodo.setDueDate(request.getDueDate());
        existingTodo.setStatus(request.getStatus());

        ToDo updatedTodo =
                toDoRepository.save(existingTodo);

        return convertToResponseDTO(updatedTodo);
    }


    // DELETE COMPLETED TODOS BY USER ID

    public void deleteByUserId(Integer userId) {

        if (userId == null) {
            throw new BadRequestException("User ID is required");
        }

        List<ToDo> todoList =
                toDoRepository.findByUserId(userId);

        if (todoList.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Todo not found for user " + userId
            );
        }

        List<ToDo> completedTodos = todoList.stream()
                .filter(todo ->
                        Constants.TODO_STATUS_COMPLETED.equalsIgnoreCase(todo.getStatus()))
                .toList();

        if (completedTodos.isEmpty()) {
            throw new BadRequestException(
                    "No completed todos found for user " + userId
            );
        }

        toDoRepository.deleteAll(completedTodos);
    }


    // ENTITY TO RESPONSE DTO

    private ToDoResponseDTO convertToResponseDTO(ToDo todo) {

        return new ToDoResponseDTO(
                todo.getUserId(),
                todo.getUserName(),
                todo.getTaskNumber(),
                todo.getDescription(),
                todo.getDueDate(),
                todo.getStatus()
        );
    }
}