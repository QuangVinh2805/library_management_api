package com.example.library_management_api.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackListResponse {
    private String userName;
    private String email;
    private String userCode;
    private String reason;
}
