package com.example.library_management_api.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ProductStatusResponse {
    private String productName;
    private Long status;
    private LocalDate updatedAt;
}
