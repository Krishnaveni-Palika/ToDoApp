package com.practice.ToDoApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ToDoResponseDTO {
    private Integer userId;
    private String userName;
    private Integer taskNumber;
    private String description;
    private LocalDate dueDate;
    private String status;
}

