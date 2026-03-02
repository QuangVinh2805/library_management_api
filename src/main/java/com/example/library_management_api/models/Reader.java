package com.example.library_management_api.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String password;
    private LocalDate birthday;
    private String name;
    private String address;
    private String email;
    private Long roleId;
    private String phone;
    private String token;
    private Long status;
    private String userCode;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String publicId;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordExpired;


}