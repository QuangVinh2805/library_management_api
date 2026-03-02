package com.example.library_management_api.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserUpdateRequest {
    private String token;
    private LocalDate birthday;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String userCode;
    private String publicId;
}
