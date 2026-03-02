package com.example.library_management_api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowResponse {
    private Long borrowedProductId;
    private String productName;
    private String userName;
    private String email;
    private String image;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private Long quantity;
    private Long isLost;
    private Long status;
    private LocalDate returnDate;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Boolean overdue;

}
