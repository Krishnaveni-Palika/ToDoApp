package com.practice.ToDoApp.ServiceTest;

import com.practice.ToDoApp.DTO.ToDoRequestDTO;
import com.practice.ToDoApp.DTO.ToDoResponseDTO;
import com.practice.ToDoApp.Entity.ToDo;
import com.practice.ToDoApp.Exception.BadRequestException;
import com.practice.ToDoApp.Exception.ResourceNotFoundException;
import com.practice.ToDoApp.Mapper.ToDoMapper;
import com.practice.ToDoApp.Repository.ToDoRepository;
import com.practice.ToDoApp.Service.ToDoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToDoServiceTest {

    @Mock
    private ToDoRepository toDoRepository;

    @Mock
    private ToDoMapper todoMapper;

    @InjectMocks
    private ToDoService toDoService;


    // CREATE TODOLIST TEST CASES

    @Test
    void createToDoList_shouldThrowException_whenRequestIsNull() {

        assertThrows(
                BadRequestException.class,
                () -> toDoService.createToDoList(null)
        );

        verifyNoInteractions(toDoRepository, todoMapper);
    }


    @Test
    void createToDoList_shouldCreateTodo_whenNewUser() {

        ToDoRequestDTO request = new ToDoRequestDTO();
        request.setUserId(101);
        request.setUserName("Krishnaveni");
        request.setTaskNumber(1);
        request.setDescription("Learn Java");

        ToDoResponseDTO expectedResponse =
                new ToDoResponseDTO();

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of());

        when(todoMapper.createToDo(request))
                .thenReturn(expectedResponse);

        ToDoResponseDTO actualResponse =
                toDoService.createToDoList(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        verify(toDoRepository).findByUserId(101);
        verify(todoMapper).createToDo(request);
    }


    @Test
    void createToDoList_shouldCreateTodo_whenExistingUserHasSameUsername() {

        ToDo existingTodo = new ToDo();
        existingTodo.setUserId(101);
        existingTodo.setUserName("Krishnaveni");
        existingTodo.setTaskNumber(1);

        ToDoRequestDTO request = new ToDoRequestDTO();
        request.setUserId(101);
        request.setUserName("Krishnaveni");
        request.setTaskNumber(2);
        request.setDescription("Learn Spring Boot");

        ToDoResponseDTO expectedResponse =
                new ToDoResponseDTO();

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of(existingTodo));

        when(todoMapper.createToDo(request))
                .thenReturn(expectedResponse);

        ToDoResponseDTO actualResponse =
                toDoService.createToDoList(request);

        assertNotNull(actualResponse);

        verify(toDoRepository).findByUserId(101);
        verify(todoMapper).createToDo(request);
    }


    @Test
    void createToDoList_shouldThrowException_whenUsernameIsChanged() {

        ToDo existingTodo = new ToDo();
        existingTodo.setUserId(101);
        existingTodo.setUserName("Krishnaveni");

        ToDoRequestDTO request = new ToDoRequestDTO();
        request.setUserId(101);
        request.setUserName("OtherUser");
        request.setTaskNumber(2);

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of(existingTodo));

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> toDoService.createToDoList(request)
                );

        assertEquals(
                "User name cannot be changed for user ID 101",
                exception.getMessage()
        );

        verify(toDoRepository).findByUserId(101);
        verifyNoInteractions(todoMapper);
    }


    @Test
    void createToDoList_shouldThrowException_whenTaskNumberAlreadyExists() {

        ToDo existingTodo = new ToDo();
        existingTodo.setUserId(101);
        existingTodo.setUserName("Krishnaveni");
        existingTodo.setTaskNumber(1);

        ToDoRequestDTO request = new ToDoRequestDTO();
        request.setUserId(101);
        request.setUserName("Krishnaveni");
        request.setTaskNumber(1);

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of(existingTodo));

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> toDoService.createToDoList(request)
                );

        assertEquals(
                "Task number 1 already exists for user 101",
                exception.getMessage()
        );

        verify(toDoRepository).findByUserId(101);
        verifyNoInteractions(todoMapper);
    }


    // GET BY USER ID TEST CASES

    @Test
    void getByUserId_shouldThrowException_whenUserIdIsNull() {

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> toDoService.getByUserId(null)
                );

        assertEquals(
                "User ID is required",
                exception.getMessage()
        );

        verifyNoInteractions(toDoRepository);
    }


    @Test
    void getByUserId_shouldReturnTodos_whenTodosExist() {

        ToDo todo1 = new ToDo();
        todo1.setUserId(101);
        todo1.setUserName("Krishnaveni");
        todo1.setTaskNumber(1);
        todo1.setDescription("Task 1");
        todo1.setStatus("PENDING");

        ToDo todo2 = new ToDo();
        todo2.setUserId(101);
        todo2.setUserName("Krishnaveni");
        todo2.setTaskNumber(2);
        todo2.setDescription("Task 2");
        todo2.setStatus("COMPLETED");

        List<ToDo> expectedTodos =
                List.of(todo1, todo2);

        when(toDoRepository.findByUserId(101))
                .thenReturn(expectedTodos);

        List<ToDoResponseDTO> actualTodos =
                toDoService.getByUserId(101);

        assertNotNull(actualTodos);
        assertEquals(2, actualTodos.size());

        assertEquals(101, actualTodos.getFirst().getUserId());
        assertEquals("Krishnaveni",
                actualTodos.get(0).getUserName());
        assertEquals(1,
                actualTodos.get(0).getTaskNumber());

        assertEquals(101, actualTodos.get(1).getUserId());
        assertEquals("Krishnaveni",
                actualTodos.get(1).getUserName());
        assertEquals(2,
                actualTodos.get(1).getTaskNumber());

        verify(toDoRepository).findByUserId(101);
    }


    @Test
    void getByUserId_shouldThrowException_whenNoTodosExist() {

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> toDoService.getByUserId(101)
                );

        assertEquals(
                "Todo not found for user 101",
                exception.getMessage()
        );

        verify(toDoRepository).findByUserId(101);
    }


    // UPDATE TODOLIST TEST CASES

    @Test
    void updateTodo_shouldThrowException_whenUserIdIsNull() {

        ToDoRequestDTO request = new ToDoRequestDTO();

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> toDoService.updateTodo(null, request)
                );

        assertEquals(
                "User ID is required",
                exception.getMessage()
        );

        verifyNoInteractions(toDoRepository);
    }


    @Test
    void updateTodo_shouldThrowException_whenRequestIsNull() {

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> toDoService.updateTodo(101, null)
                );

        assertEquals(
                "Todo request is required",
                exception.getMessage()
        );

        verifyNoInteractions(toDoRepository);
    }


    @Test
    void updateTodo_shouldUpdateTodo_whenValidRequest() {

        ToDo existingTodo = new ToDo();
        existingTodo.setUserId(101);
        existingTodo.setUserName("Krishnaveni");
        existingTodo.setTaskNumber(1);
        existingTodo.setDescription("Old task");
        existingTodo.setStatus("PENDING");

        ToDoRequestDTO request = new ToDoRequestDTO();
        request.setUserId(101);
        request.setUserName("Krishnaveni");
        request.setTaskNumber(1);
        request.setDescription("Updated task");
        request.setStatus("COMPLETED");

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of(existingTodo));

        when(toDoRepository.save(existingTodo))
                .thenReturn(existingTodo);

        ToDoResponseDTO response =
                toDoService.updateTodo(101, request);

        assertNotNull(response);

        assertEquals(101, response.getUserId());
        assertEquals("Krishnaveni",
                response.getUserName());
        assertEquals(1,
                response.getTaskNumber());
        assertEquals("Updated task",
                response.getDescription());
        assertEquals("COMPLETED",
                response.getStatus());

        verify(toDoRepository).findByUserId(101);
        verify(toDoRepository).save(existingTodo);
    }


    @Test
    void updateTodo_shouldThrowException_whenTaskDoesNotExist() {

        ToDo existingTodo = new ToDo();
        existingTodo.setUserId(101);
        existingTodo.setUserName("Krishnaveni");
        existingTodo.setTaskNumber(1);

        ToDoRequestDTO request = new ToDoRequestDTO();
        request.setUserId(101);
        request.setUserName("Krishnaveni");
        request.setTaskNumber(2);

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of(existingTodo));

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> toDoService.updateTodo(101, request)
                );

        assertEquals(
                "Task 2 not found for user 101",
                exception.getMessage()
        );

        verify(toDoRepository).findByUserId(101);
        verify(toDoRepository, never()).save(existingTodo);
    }


    @Test
    void updateTodo_shouldThrowException_whenUsernameIsChanged() {

        ToDo existingTodo = new ToDo();
        existingTodo.setUserId(101);
        existingTodo.setUserName("Krishnaveni");
        existingTodo.setTaskNumber(1);

        ToDoRequestDTO request = new ToDoRequestDTO();
        request.setUserId(101);
        request.setUserName("OtherUser");
        request.setTaskNumber(1);

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of(existingTodo));

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> toDoService.updateTodo(101, request)
                );

        assertEquals(
                "User name cannot be changed for user ID 101",
                exception.getMessage()
        );

        verify(toDoRepository).findByUserId(101);
        verify(toDoRepository, never()).save(existingTodo);
    }

    // DELETE TODOLIST TEST CASES

    @Test
    void deleteByUserId_shouldThrowException_whenUserIdIsNull() {

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> toDoService.deleteByUserId(null)
                );

        assertEquals(
                "User ID is required",
                exception.getMessage()
        );

        verifyNoInteractions(toDoRepository);
    }


    @Test
    void deleteByUserId_shouldThrowException_whenNoTodosExist() {

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> toDoService.deleteByUserId(101)
                );

        assertEquals(
                "Todo not found for user 101",
                exception.getMessage()
        );

        verify(toDoRepository).findByUserId(101);
        verify(toDoRepository, never()).deleteAll(anyList());
    }


    @Test
    void deleteByUserId_shouldThrowException_whenNoCompletedTodosExist() {

        ToDo todo = new ToDo();
        todo.setUserId(101);
        todo.setStatus("PENDING");

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of(todo));

        BadRequestException exception =
                assertThrows(
                        BadRequestException.class,
                        () -> toDoService.deleteByUserId(101)
                );

        assertEquals(
                "No completed todos found for user 101",
                exception.getMessage()
        );

        verify(toDoRepository).findByUserId(101);
        verify(toDoRepository, never()).deleteAll(anyList());
    }


    @Test
    void deleteByUserId_shouldDeleteCompletedTodos() {

        ToDo pendingTodo = new ToDo();
        pendingTodo.setUserId(101);
        pendingTodo.setTaskNumber(1);
        pendingTodo.setStatus("PENDING");

        ToDo completedTodo = new ToDo();
        completedTodo.setUserId(101);
        completedTodo.setTaskNumber(2);
        completedTodo.setStatus("COMPLETED");

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of(
                        pendingTodo,
                        completedTodo
                ));

        toDoService.deleteByUserId(101);

        verify(toDoRepository).findByUserId(101);

        verify(toDoRepository).deleteAll(
                List.of(completedTodo)
        );
    }
}