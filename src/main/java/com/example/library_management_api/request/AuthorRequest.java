package com.example.library_management_api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequest {
    private String authorName;
    private String address;
    private String birthday;
}
