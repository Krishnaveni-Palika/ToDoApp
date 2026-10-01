package com.practice.ToDoApp.ControllerTest;

import com.practice.ToDoApp.Controller.ToDoController;
import com.practice.ToDoApp.DTO.ToDoRequestDTO;
import com.practice.ToDoApp.DTO.ToDoResponseDTO;
import com.practice.ToDoApp.Service.ToDoService;
import com.practice.ToDoApp.Exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToDoController.class)
@Import(GlobalExceptionHandler.class)
class ToDoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ToDoService toDoService;


    // CREATE TODO

    @Test
    void createTodo_shouldReturnCreated_whenRequestIsValid()
            throws Exception {

        ToDoResponseDTO response =
                new ToDoResponseDTO(
                        101,
                        "John",
                        1,
                        "Complete assignment",
                        LocalDate.of(2026, 10, 5),
                        "PENDING"
                );

        when(toDoService.createToDoList(any(ToDoRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/todos")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                    {
                                      "userId": 101,
                                      "userName": "John",
                                      "taskNumber": 1,
                                      "description": "Complete assignment",
                                      "dueDate": "2026-10-05",
                                      "status": "PENDING"
                                    }
                                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(101))
                .andExpect(jsonPath("$.userName").value("John"))
                .andExpect(jsonPath("$.taskNumber").value(1))
                .andExpect(jsonPath("$.description")
                        .value("Complete assignment"))
                .andExpect(jsonPath("$.dueDate")
                        .value("2026-10-05"))
                .andExpect(jsonPath("$.status")
                        .value("PENDING"));

        verify(toDoService)
                .createToDoList(any(ToDoRequestDTO.class));
    }


    @Test
    void createTodo_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post("/todos")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                    {
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(toDoService);
    }


    // GET TODO

    @Test
    void getTodosByUserId_shouldReturnTodos_whenUserExists()
            throws Exception {

        ToDoResponseDTO response =
                new ToDoResponseDTO(
                        101,
                        "John",
                        1,
                        "Complete assignment",
                        LocalDate.of(2026, 10, 5),
                        "PENDING"
                );

        when(toDoService.getByUserId(101))
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/todos/101")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(101))
                .andExpect(jsonPath("$[0].userName").value("John"))
                .andExpect(jsonPath("$[0].taskNumber").value(1))
                .andExpect(jsonPath("$[0].description")
                        .value("Complete assignment"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(toDoService)
                .getByUserId(101);
    }


    @Test
    void getTodosByUserId_shouldReturnBadRequest_whenUserIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        get("/todos/0")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(toDoService);
    }


    // UPDATE TODO

    @Test
    void updateTodo_shouldReturnUpdatedTodo_whenRequestIsValid()
            throws Exception {

        ToDoResponseDTO response =
                new ToDoResponseDTO(
                        101,
                        "John",
                        1,
                        "Complete assignment",
                        LocalDate.of(2026, 10, 5),
                        "COMPLETED"
                );

        when(toDoService.updateTodo(
                eq(101),
                any(ToDoRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/todos/101")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                    {
                                      "userId": 101,
                                      "userName": "John",
                                      "taskNumber": 1,
                                      "description": "Complete assignment",
                                      "dueDate": "2026-10-05",
                                      "status": "COMPLETED"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(101))
                .andExpect(jsonPath("$.taskNumber").value(1))
                .andExpect(jsonPath("$.status")
                        .value("COMPLETED"));

        verify(toDoService)
                .updateTodo(
                        eq(101),
                        any(ToDoRequestDTO.class));
    }


    @Test
    void updateTodo_shouldReturnBadRequest_whenUserIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        patch("/todos/0")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                    {
                                      "userId": 101,
                                      "userName": "John",
                                      "taskNumber": 1,
                                      "description": "Complete assignment",
                                      "dueDate": "2026-10-05",
                                      "status": "COMPLETED"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(toDoService);
    }


    @Test
    void updateTodo_shouldReturnBadRequest_whenRequestBodyIsInvalid()
            throws Exception {

        mockMvc.perform(
                        patch("/todos/101")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                    {
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(toDoService);
    }


    // DELETE TODO

    @Test
    void deleteCompletedTodos_shouldReturnNoContent_whenDeleteIsSuccessful()
            throws Exception {

        doNothing()
                .when(toDoService)
                .deleteByUserId(101);

        mockMvc.perform(
                        delete("/todos/101")
                )
                .andExpect(status().isNoContent());

        verify(toDoService)
                .deleteByUserId(101);
    }


    @Test
    void deleteCompletedTodos_shouldReturnBadRequest_whenUserIdIsInvalid()
            throws Exception {

        mockMvc.perform(
                        delete("/todos/0")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(toDoService);
    }
}