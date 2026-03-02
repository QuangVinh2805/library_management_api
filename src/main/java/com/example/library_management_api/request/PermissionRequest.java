package com.example.library_management_api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequest {
    private String name;
    private String code;
    private String route;
    private String method;
    private String routeName;

}
