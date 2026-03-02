package com.example.library_management_api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateUserResponse {
    private String token;
    private List<String> roleNames;
}
