package com.example.library_management_api.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateResponse {
    private LocalDate birthday;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String userCode;

}
