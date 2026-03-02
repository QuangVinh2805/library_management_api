package com.example.library_management_api.request;

import lombok.Data;

@Data
public class LoginRequest {
    public String email;
    public String password;
}
