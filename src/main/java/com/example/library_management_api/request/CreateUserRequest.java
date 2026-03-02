package com.example.library_management_api.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class CreateUserRequest {
    private LocalDate birthday;
    private String name;
    private String address;
    private String email;
    private String phone;
    private List<Long> roleIds;
    private Long status;
    private String userCode;
}

