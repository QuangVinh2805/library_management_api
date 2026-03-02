package com.example.library_management_api.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionResponse {
    private Long id;
    private String role;
    private String name;
    private String endPoint;
    private String method;
    private String route;
    private String description;
}
