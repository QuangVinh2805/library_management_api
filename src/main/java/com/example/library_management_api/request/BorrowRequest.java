package com.example.library_management_api.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class BorrowRequest {
    private String email;
    private List<BorrowItemRequest> items;
}
