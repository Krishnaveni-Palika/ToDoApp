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

import java.time.LocalDate;
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
    void createToDoList_shouldCreateTodo_whenUserDoesNotExist() {

        ToDoRequestDTO request = new ToDoRequestDTO();

        request.setUserId(101);
        request.setUserName("Krishnaveni");
        request.setTaskNumber(1);
        request.setDescription("Complete LeetCode");
        request.setDueDate(LocalDate.of(2026, 10, 5));
        request.setStatus("CREATED");

        ToDoResponseDTO expectedResponse = new ToDoResponseDTO(
                101,
                "Krishnaveni",
                1,
                "Complete LeetCode",
                LocalDate.of(2026, 10, 5),
                "CREATED"
        );

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
    void createToDoList_shouldCreateTodo_whenUsernameIsSame() {

        ToDo existingTodo = new ToDo();

        existingTodo.setUserId(101);
        existingTodo.setUserName("Krishnaveni");
        existingTodo.setTaskNumber(1);

        ToDoRequestDTO request = new ToDoRequestDTO();

        request.setUserId(101);
        request.setUserName("Krishnaveni");
        request.setTaskNumber(2);
        request.setDescription("Complete assignments");
        request.setDueDate(LocalDate.of(2026, 10, 5));
        request.setStatus("CREATED");

        ToDoResponseDTO expectedResponse = new ToDoResponseDTO(
                101,
                "Krishnaveni",
                2,
                "Complete assignments",
                LocalDate.of(2026, 10, 5),
                "CREATED"
        );

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of(existingTodo));

        when(todoMapper.createToDo(request))
                .thenReturn(expectedResponse);

        ToDoResponseDTO actualResponse =
                toDoService.createToDoList(request);

        assertEquals(expectedResponse, actualResponse);

        verify(todoMapper).createToDo(request);
    }


    @Test
    void createToDoList_shouldThrowException_whenUsernameIsChanged() {

        ToDo existingTodo = new ToDo();

        existingTodo.setUserId(101);
        existingTodo.setUserName("Krishnaveni");
        existingTodo.setTaskNumber(1);

        ToDoRequestDTO request = new ToDoRequestDTO();

        request.setUserId(101);
        request.setUserName("John");
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

        verify(todoMapper, never()).createToDo(request);
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

        verify(todoMapper, never()).createToDo(request);
    }


    // GET BY USER ID TEST CASES

    @Test
    void getByUserId_shouldThrowException_whenUserIdIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> toDoService.getByUserId(null)
        );

        verifyNoInteractions(toDoRepository);
    }


    @Test
    void getByUserId_shouldReturnTodos_whenTodosExist() {

        ToDo todo1 = new ToDo();
        todo1.setUserId(101);
        todo1.setUserName("Krishnaveni");
        todo1.setTaskNumber(1);

        ToDo todo2 = new ToDo();
        todo2.setUserId(101);
        todo2.setUserName("Krishnaveni");
        todo2.setTaskNumber(2);

        List<ToDo> expectedTodos = List.of(todo1, todo2);

        when(toDoRepository.findByUserId(101))
                .thenReturn(expectedTodos);

        List<ToDo> actualTodos =
                toDoService.getByUserId(101);

        assertNotNull(actualTodos);
        assertEquals(expectedTodos, actualTodos);
        assertEquals(2, actualTodos.size());

        verify(toDoRepository).findByUserId(101);
    }


    @Test
    void getByUserId_shouldThrowException_whenNoTodosExist() {

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> toDoService.getByUserId(101)
                );

        assertEquals(
                "User does not exist",
                exception.getMessage()
        );
    }



    // UPDATE TEST CASES


    @Test
    void updateTodo_shouldThrowException_whenRequestIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> toDoService.updateTodo(101, null)
        );

        verifyNoInteractions(toDoRepository);
    }


    @Test
    void updateTodo_shouldUpdateTodo_whenTaskExists() {

        ToDo existingTodo = new ToDo();

        existingTodo.setUserId(101);
        existingTodo.setUserName("Krishnaveni");
        existingTodo.setTaskNumber(1);
        existingTodo.setDescription("Old description");
        existingTodo.setDueDate(LocalDate.of(2026, 10, 5));
        existingTodo.setStatus("CREATED");

        ToDoRequestDTO request = new ToDoRequestDTO();

        request.setUserId(101);
        request.setUserName("Krishnaveni");
        request.setTaskNumber(1);
        request.setDescription("Updated description");
        request.setDueDate(LocalDate.of(2026, 10, 10));
        request.setStatus("COMPLETED");

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of(existingTodo));

        when(toDoRepository.save(existingTodo))
                .thenReturn(existingTodo);

        ToDoResponseDTO actualResponse =
                toDoService.updateTodo(101, request);

        assertNotNull(actualResponse);

        assertEquals(101, actualResponse.getUserId());
        assertEquals("Krishnaveni", actualResponse.getUserName());
        assertEquals(1, actualResponse.getTaskNumber());
        assertEquals(
                "Updated description",
                actualResponse.getDescription()
        );
        assertEquals(
                LocalDate.of(2026, 10, 10),
                actualResponse.getDueDate()
        );
        assertEquals(
                "COMPLETED",
                actualResponse.getStatus()
        );

        verify(toDoRepository).findByUserId(101);
        verify(toDoRepository).save(existingTodo);
    }


    @Test
    void updateTodo_shouldThrowException_whenTaskDoesNotExist() {

        ToDoRequestDTO request = new ToDoRequestDTO();

        request.setTaskNumber(5);
        request.setUserName("Krishnaveni");

        when(toDoRepository.findByUserId(101))
                .thenReturn(List.of());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> toDoService.updateTodo(101, request)
                );

        assertEquals(
                "Task 5 not found for user 101",
                exception.getMessage()
        );

        verify(toDoRepository, never()).save(any());
    }


    @Test
    void updateTodo_shouldThrowException_whenUsernameIsChanged() {

        ToDo existingTodo = new ToDo();

        existingTodo.setUserId(101);
        existingTodo.setUserName("Krishnaveni");
        existingTodo.setTaskNumber(1);

        ToDoRequestDTO request = new ToDoRequestDTO();

        request.setUserId(101);
        request.setUserName("John");
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

        verify(toDoRepository, never()).save(existingTodo);
    }

    // DELETE TEST CASES

    @Test
    void deleteByUserId_shouldThrowException_whenUserIdIsNull() {

        assertThrows(
                BadRequestException.class,
                () -> toDoService.deleteByUserId(null)
        );

        verifyNoInteractions(toDoRepository);
    }


    @Test
    void deleteByUserId_shouldThrowException_whenUserHasNoTodos() {

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
        verify(toDoRepository, never()).deleteAll(any());
    }


    @Test
    void deleteByUserId_shouldThrowException_whenNoCompletedTodosExist() {

        ToDo todo = new ToDo();

        todo.setUserId(101);
        todo.setTaskNumber(1);
        todo.setStatus("IN-PROGRESS");

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

        verify(toDoRepository, never()).deleteAll(any());
    }


    @Test
    void deleteByUserId_shouldDeleteCompletedTodos() {

        ToDo completedTodo = new ToDo();

        completedTodo.setUserId(101);
        completedTodo.setTaskNumber(1);
        completedTodo.setStatus("COMPLETED");

        ToDo pendingTodo = new ToDo();

        pendingTodo.setUserId(101);
        pendingTodo.setTaskNumber(2);
        pendingTodo.setStatus("IN-PROGRESS");

        List<ToDo> todos =
                List.of(completedTodo, pendingTodo);

        when(toDoRepository.findByUserId(101))
                .thenReturn(todos);

        toDoService.deleteByUserId(101);

        verify(toDoRepository).findByUserId(101);

        verify(toDoRepository).deleteAll(
                List.of(completedTodo)
        );
    }
}