package com.example.library_management_api.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class ReturnResponse {
    private String productName;
    private String userName;
    private String userCode;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private Long status;
    private Long isLost;
    private Long quantity;
    private Long lateFee;
    private Long compensationFee;
    private Long hasOverdueFeePaid;
    private Long hasLostItemFeePaid;
    private Long lateDay;
}
