package com.example.library_management_api.response;


import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ProductReservationResponse {
    private Long id;
    private String name;
    private String email;
    private String productName;
    private String image;
    private Long quantity;
    private LocalDate borrowDay;
    private Long status;
    private LocalDate dueDay;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Boolean overdue;
}
