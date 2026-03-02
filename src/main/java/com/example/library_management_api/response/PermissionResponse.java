package com.example.library_management_api.response;

import lombok.Data;

@Data
public class PermissionResponse {
    private Long id;
    private String name;
    private String code;
    private String endPoint;
    private String method;
    private String routeName;
}
