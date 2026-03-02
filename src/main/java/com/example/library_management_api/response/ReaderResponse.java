package com.example.library_management_api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReaderResponse {
    private String token;
    private LocalDate birthday;
    private String email;
    private String phone;
    private String name;
    private String userCode;
    private Long status;
    private String address;
    private String roleName;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String publicId;

    public static ReaderResponse fromEntity(
            String token,
            String roleName,
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
        return new ReaderResponse(
                token,
                birthday,
                email,
                phone,
                name,
                userCode,
                status,
                address,
                roleName,
                createdAt,
                updatedAt,
                publicId
        );
    }
}
