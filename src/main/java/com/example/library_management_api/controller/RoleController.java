package com.example.library_management_api.controller;

import com.example.library_management_api.models.Role;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.response.ReaderLoginResponse;
import com.example.library_management_api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @GetMapping("/getAll")
    public MyApiResponse<Map<String, Object>> getAllRole(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return roleService.getAllRoles(pageable);
    }

    @GetMapping("/getAllByStatus")
    public MyApiResponse<Map<String, Object>> getAllRoleByStatus(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return roleService.getAllRolesByStatus(pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<MyApiResponse<Role>> create(@RequestParam String roleName) {
        MyApiResponse<Role> response =
                roleService.create(roleName);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<MyApiResponse<Role>> update(
            @RequestParam Long id,
            @RequestParam String roleName) {
        MyApiResponse<Role> response =
                roleService.update(id,roleName);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/changeStatus")
    public MyApiResponse<Role> changeStatus(
            @RequestParam Long id ) {

        return roleService.changeStatus(id);
    }


    @GetMapping("/getSearchAllRole")
    public MyApiResponse<Map<String, Object>> getSearchAllRole(@RequestParam String keyword,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
        return roleService.getSearchAllRole(keyword, pageable);
    }
}

