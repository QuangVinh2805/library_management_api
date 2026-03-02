package com.example.library_management_api.controller;

import com.example.library_management_api.request.PermissionRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.response.PermissionResponse;
import com.example.library_management_api.response.RouteResponse;
import com.example.library_management_api.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    PermissionService permissionService;

    @GetMapping("/getAll")
    public MyApiResponse<Map<String, Object>> getAllPermission(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return permissionService.getAllPermission(pageable);
    }

    @GetMapping("/getSearchAllPermission")
    public MyApiResponse<Map<String, Object>> getSearchAllPermission(@RequestParam String keyword,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return permissionService.SearchAllPermission(keyword, pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<MyApiResponse<PermissionResponse>> create(
            @RequestBody PermissionRequest request) {
        MyApiResponse<PermissionResponse> response =
                permissionService.create(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<MyApiResponse<PermissionResponse>> update(
            @RequestParam Long id,
            @RequestBody PermissionRequest request) {

        MyApiResponse<PermissionResponse> response =
                permissionService.update(id,request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<MyApiResponse<?>> delete(@RequestParam String code) {
        MyApiResponse<?> response =
                permissionService.delete(code);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/route")
    public MyApiResponse<RouteResponse> getRouteByPermission(
            @RequestParam String code) {
        return permissionService.getRouteByPermission(code);
    }

    @GetMapping("/role/permissions")
    public MyApiResponse<Map<String, Object>> listPermissionsByRole(
            @RequestParam Long roleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return permissionService.listPermissionsByRole(roleId, pageable);
    }



    @DeleteMapping("/role/permissions/remove")
    public ResponseEntity<MyApiResponse<Void>> removePermissionFromRole(
            @RequestParam Long roleId,
            @RequestParam Long permissionId
    ) {
        MyApiResponse<Void> response =
                permissionService.removePermissionFromRole(roleId, permissionId);

        return ResponseEntity.status(response.getStatus()).body(response);
    }



}

