package com.example.library_management_api.request;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String token;
    private String oldPassword;
    private String newPassword;
}
