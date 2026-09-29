package com.practice.ToDoApp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
public class ToDoRequestDTO {

    @NotNull(message = "User ID is required")
    @Positive
    private Integer userId;

    @NotBlank(message = "User name is required")
    private String userName;

    @NotNull(message = "Task number is required")
    private Integer taskNumber;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotBlank(message = "Status is required")
    private String status;

    // getters and setters
}
