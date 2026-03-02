package com.example.library_management_api.request;


import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ProductReservationRequest {
    private String token;
    private String hashId;
    private Long quantity;
    private LocalDate borrowDay;
    private LocalDate dueDay;

}
