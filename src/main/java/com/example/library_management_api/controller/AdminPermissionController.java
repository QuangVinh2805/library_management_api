package com.example.library_management_api.controller;


import com.example.library_management_api.request.AssignPermissionRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.response.RolePermissionResponse;
import com.example.library_management_api.service.AdminPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminPermissionController {
    @Autowired
    private AdminPermissionService service;


    @Autowired
    AdminPermissionService adminPermissionService;



    @PostMapping("/assign-permission/role")
    public ResponseEntity<MyApiResponse<?>> assignToRole(
            @RequestBody AssignPermissionRequest request
    ) {
        MyApiResponse<?> response =
                service.assignPermissionToRole(request);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }


    @GetMapping("/role/findPermissions")
    public MyApiResponse<?> getPermissionsByRole(
            @RequestParam Long roleId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return adminPermissionService.getPermissionsByRole(roleId, keyword, pageable);
    }
}
