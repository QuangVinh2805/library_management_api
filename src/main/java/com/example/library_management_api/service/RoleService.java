package com.example.library_management_api.service;

import com.example.library_management_api.models.Category;
import com.example.library_management_api.models.Role;
import com.example.library_management_api.models.Route;
import com.example.library_management_api.repository.RoleRepository;
import com.example.library_management_api.response.MyApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public MyApiResponse<Map<String, Object>> getAllRoles(Pageable pageable){
        Page<Role> page = roleRepository.findAllRole(pageable);
        List<Role> role = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", role);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách quyền thành công",result);
    }

    public MyApiResponse<Map<String, Object>> getAllRolesByStatus(Pageable pageable){
        Page<Role> page = roleRepository.findAllRoleByStatus(pageable);
        List<Role> role = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", role);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách quyền thành công",result);
    }

    public MyApiResponse<Map<String, Object>> getSearchAllRole(String keyword,Pageable pageable){
        Page<Role> page = roleRepository.searchAllRolesByName(keyword,pageable);
        List<Role> roles = page.getContent();
        Map<String, Object> result = new HashMap<>();
        result.put("data", roles);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách vai trò thành công",result);
    }

    public MyApiResponse<Role> create(String roleName) {

        if (roleName == null || roleName.trim().isEmpty()) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Tên role không được để trống"
            );
        }

        if (roleRepository.existsByRoleName(roleName.trim())) {
            return MyApiResponse.error(
                    HttpStatus.CONFLICT,
                    "Role đã tồn tại"
            );
        }

        Role role = new Role();
        role.setRoleName(roleName.trim());
        role.setStatus(1L);
        role.setCreatedAt(new Date());

        roleRepository.save(role);

        return MyApiResponse.success("Tạo role thành công", role);
    }


    public MyApiResponse<Role> update(Long id, String roleName) {

        if (id == null) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Id không được null"
            );
        }

        if (roleName == null || roleName.trim().isEmpty()) {
            return MyApiResponse.error(
                    HttpStatus.BAD_REQUEST,
                    "Tên role không được để trống"
            );
        }

        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) {
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy role"
            );
        }

        role.setRoleName(roleName.trim());
        role.setUpdatedAt(new Date());

        roleRepository.save(role);

        return MyApiResponse.success("Cập nhật role thành công", role);
    }



    public MyApiResponse<Role> changeStatus(Long id) {

        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy role");
        }

        role.setStatus(role.getStatus() == 1L ? 0L : 1L);
        role.setUpdatedAt(new Date());

        roleRepository.save(role);

        return MyApiResponse.success("Đổi trạng thái thành công", role);
    }
}

