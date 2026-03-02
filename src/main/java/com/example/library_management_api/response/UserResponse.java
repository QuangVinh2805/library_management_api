package com.example.library_management_api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String token;
    private LocalDate birthday;
    private String email;
    private String phone;
    private String name;
    private String userCode;
    private Long status;
    private String address;
    private List<RoleResponse> roleName;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String publicId;

    public static UserResponse fromEntity(
            String token,
            List<RoleResponse> roles,
            LocalDate birthday,
            String email,
            String phone,
            String name,
            String userCode,
            Long status,
            String address,
            LocalDate createdAt,
            LocalDate updatedAt,
            String publicId
    ) {
        return new UserResponse(
                token,
                birthday,
                email,
                phone,
                name,
                userCode,
                status,
                address,
                roles,
                createdAt,
                updatedAt,
                publicId
        );
    }


}
