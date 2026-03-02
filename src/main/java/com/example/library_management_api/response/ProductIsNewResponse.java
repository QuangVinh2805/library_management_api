package com.example.library_management_api.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ProductIsNewResponse {
    private String productName;
    private Long isNew;
    private LocalDate updatedAt;
}
