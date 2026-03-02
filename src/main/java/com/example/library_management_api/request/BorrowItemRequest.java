package com.example.library_management_api.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BorrowItemRequest {
    private String hashId;
    private Integer quantity;
    private LocalDate dueDate;

}
