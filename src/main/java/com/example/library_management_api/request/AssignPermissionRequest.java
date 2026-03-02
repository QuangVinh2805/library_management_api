package com.example.library_management_api.request;

import lombok.Data;

import java.util.List;

@Data
public class AssignPermissionRequest {
    private Long roleId;
    private List<Long> permissionIds;}
