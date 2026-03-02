package com.example.library_management_api.service;


import com.example.library_management_api.models.RolePermission;
import com.example.library_management_api.models.User;
import com.example.library_management_api.repository.PermissionRepository;
import com.example.library_management_api.repository.RolePermissionRepository;
import com.example.library_management_api.repository.UserRepository;
import com.example.library_management_api.request.AssignPermissionRequest;
import com.example.library_management_api.response.MyApiResponse;
import com.example.library_management_api.response.RolePermissionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminPermissionService {

    @Autowired
    PermissionRepository permissionRepository;



    @Autowired
    RolePermissionRepository rolePermissionRepository;

    @Autowired
    UserRepository userRepository;


//    public MyApiResponse<?> assignPermissionToRole(Long roleId, Long permissionId) {
//
//        if(permissionId == null){
//            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Bạn chưa chọn quyền muốn gắn");
//        }
//
//        if (rolePermissionRepository
//                .existsByRoleIdAndPermissionId(roleId, permissionId)) {
//            return MyApiResponse.error(
//                    HttpStatus.BAD_REQUEST,
//                    "Role đã có quyền này"
//            );
//        }
//
//        RolePermission rolePermission = RolePermission.builder()
//                .roleId(roleId)
//                .permissionId(permissionId)
//                .build();
//
//        rolePermissionRepository.save(rolePermission);
//
//        return MyApiResponse.success("Gán quyền thành công", null);
//    }


    public MyApiResponse<?> assignPermissionToRole(AssignPermissionRequest request) {

        if (request.getRoleId() == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Thiếu roleId");
        }

        if (request.getPermissionIds() == null || request.getPermissionIds().isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Bạn chưa chọn quyền");
        }

        List<Long> permissionIds = request.getPermissionIds();

        // lấy list đã tồn tại
        List<Long> existed = rolePermissionRepository
                .findPermissionIdsByRoleIdAndPermissionIdIn(
                        request.getRoleId(),
                        permissionIds
                );

        // filter những cái chưa có
        List<RolePermission> toSave = permissionIds.stream()
                .filter(id -> !existed.contains(id))
                .map(id -> RolePermission.builder()
                        .roleId(request.getRoleId())
                        .permissionId(id)
                        .build())
                .toList();

        if (toSave.isEmpty()) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST,
                    "Tất cả permission đã tồn tại");
        }

        rolePermissionRepository.saveAll(toSave);

        return MyApiResponse.success(
                "Gán thành công " + toSave.size() + " permission",
                null
        );
    }


    public MyApiResponse<Map<String, Object>> getPermissionsByRole(
            Long roleId,
            String keyword,
            Pageable pageable
    ) {

        Page<Object[]> page =
                rolePermissionRepository.findPermissionByRole(roleId, keyword, pageable);

        List<RolePermissionResponse> permissions = page.map(obj -> {
            RolePermissionResponse res = new RolePermissionResponse();

            res.setId(((Number) obj[0]).longValue());
            res.setRole((String) obj[1]);
            res.setName((String) obj[2]);
            res.setEndPoint((String) obj[3]);
            res.setMethod((String) obj[4]);
            res.setRoute((String) obj[5]);
            res.setDescription((String) obj[6]);

            return res;
        }).getContent();

        Map<String, Object> result = new HashMap<>();
        result.put("data", permissions);
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success("Tìm kiếm permission thành công", result);
    }


}
