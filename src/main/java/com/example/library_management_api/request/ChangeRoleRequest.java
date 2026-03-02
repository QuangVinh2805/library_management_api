package com.example.library_management_api.request;


import lombok.Data;

import java.util.List;

@Data
public class ChangeRoleRequest {
    private String publicId;
    private List<String> targetRoles;
}
