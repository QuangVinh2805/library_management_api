package com.example.library_management_api.service;


import com.example.library_management_api.models.*;
import com.example.library_management_api.repository.*;
import com.example.library_management_api.request.PermissionRequest;
import com.example.library_management_api.response.*;
import com.example.library_management_api.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    UserService userService;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    RolePermissionRepository rolePermissionRepository;


    @Autowired
    UserRepository userRepository;

    private final AntPathMatcher matcher = new AntPathMatcher();

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }


    public boolean checkPermission(Long id, String uri, String method) {

        uri = uri.trim();
        method = method.toUpperCase();

        String type = SecurityUtil.getCurrentType();
        if (type == null) return false;

        List<Permission> permissions = new ArrayList<>();

        if ("USER".equals(type)) {
            permissions.addAll(permissionRepository.findPermissionsByUser(id));
        }

        if ("READER".equals(type)) {
            permissions.addAll(permissionRepository.findPermissionsByReader(id));
        }

        String finalMethod = method;
        String finalUri = uri;
        return permissions.stream().anyMatch(p ->
                p.getMethod().equalsIgnoreCase(finalMethod)
                        && matcher.match(p.getRoute(), finalUri)
        );
    }

    public MyApiResponse<PermissionResponse> create(PermissionRequest request) {

        if (request == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ");
        }

        if (isBlank(request.getCode())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Permission code không được để trống");
        }

        if (isBlank(request.getRoute())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Route không được để trống");
        }

        if (isBlank(request.getMethod())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Method không được để trống");
        }

        if (permissionRepository.findByCode(request.getCode().trim()) != null) {
            return MyApiResponse.error(HttpStatus.CONFLICT, "Permission code đã tồn tại");
        }

        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setCode(request.getCode().trim());
        permission.setRoute(request.getRoute().trim());
        permission.setMethod(request.getMethod().trim());

        // ===== SET ROUTE_ID =====
        if (!isBlank(request.getRouteName())) {
            Route route = routeRepository.findByRoute(request.getRouteName().trim());
            if (route == null) {
                return MyApiResponse.error(HttpStatus.NOT_FOUND, "Route không tồn tại");
            }
            permission.setRouteId(route.getId());
        } else {
            permission.setRouteId(null);
        }

        permissionRepository.save(permission);

        return MyApiResponse.success(
                "Tạo permission thành công",
                mapToResponse(permission)
        );
    }



    public MyApiResponse<PermissionResponse> update(Long id, PermissionRequest request) {

        if (id == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Id không được null");
        }

        if (request == null) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ");
        }

        if (isBlank(request.getCode())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Permission code không được để trống");
        }

        if (isBlank(request.getRoute())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Route không được để trống");
        }

        if (isBlank(request.getMethod())) {
            return MyApiResponse.error(HttpStatus.BAD_REQUEST, "Method không được để trống");
        }

        Permission permission = permissionRepository.findById(id).orElse(null);
        if (permission == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Permission không tồn tại");
        }

        Permission existCode = permissionRepository.findByCode(request.getCode().trim());
        if (existCode != null && !existCode.getId().equals(permission.getId())) {
            return MyApiResponse.error(HttpStatus.CONFLICT, "Permission code đã tồn tại");
        }

        // ===== ROUTE_ID =====
        if (isBlank(request.getRouteName())) {
            permission.setRouteId(null);
        } else {
            Route route = routeRepository.findByRoute(request.getRouteName().trim());
            if (route == null) {
                return MyApiResponse.error(HttpStatus.NOT_FOUND, "Route không tồn tại");
            }
            permission.setRouteId(route.getId());
        }

        permission.setName(request.getName());
        permission.setCode(request.getCode().trim());
        permission.setRoute(request.getRoute().trim());
        permission.setMethod(request.getMethod().trim());

        permissionRepository.save(permission);

        return MyApiResponse.success(
                "Cập nhật permission thành công",
                mapToResponse(permission)
        );
    }



    public MyApiResponse<Map<String, Object>> SearchAllPermission(String keyword, Pageable pageable) {
        Page<Permission> page = permissionRepository.searchAllPermissions(keyword,pageable);
        List<Permission> permissions = page.getContent();

        if(permissions.isEmpty()){
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Khônc tìm thấy end-point");
        }

        List<PermissionResponse> permissionResponses = permissions.stream().map(
                        p -> {
                            PermissionResponse permissionResponse = new PermissionResponse();
                            permissionResponse.setName(p.getName());
                            permissionResponse.setId(p.getId());
                            permissionResponse.setEndPoint(p.getRoute());
                            permissionResponse.setCode(p.getCode());
                            permissionResponse.setMethod(p.getMethod());
                            Route route = null;
                            if (p.getRouteId() != null) {
                                route = routeRepository.findRouteById(p.getRouteId());
                            }

                            permissionResponse.setRouteName(
                                    route != null ? route.getRoute() : null
                            );

                            return permissionResponse;
                        })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", permissionResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách end-point thành công", result);
    }


    public MyApiResponse<Map<String, Object>> getAllPermission(Pageable pageable) {
        Page<Permission> page = permissionRepository.findAllPermission(pageable);
        List<Permission> permissions = page.getContent();

        if(permissions.isEmpty()){
            return MyApiResponse.error(HttpStatus.NOT_FOUND,"Khônc tìm thấy end-point");
        }

        List<PermissionResponse> permissionResponses = permissions.stream()
                .map(p -> {

                    PermissionResponse permissionResponse = new PermissionResponse();
                    permissionResponse.setId(p.getId());
                    permissionResponse.setName(p.getName());
                    permissionResponse.setEndPoint(p.getRoute());
                    permissionResponse.setCode(p.getCode());
                    permissionResponse.setMethod(p.getMethod());

                    // route có thể null
                    Route route = null;
                    if (p.getRouteId() != null) {
                        route = routeRepository.findRouteById(p.getRouteId());
                    }

                    permissionResponse.setRouteName(
                            route != null ? route.getRoute() : null
                    );

                    return permissionResponse;
                })
                .collect(Collectors.toList());


        Map<String, Object> result = new HashMap<>();
        result.put("data", permissionResponses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return MyApiResponse.success("Lấy danh sách end-point thành công", result);
    }

    public MyApiResponse<?> delete(String code) {

        Permission permission = permissionRepository.findByCode(code);
        if (permission == null) {
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Permission không tồn tại"
            );
        }

        Long permissionId = permission.getId();

        boolean usedInRole = rolePermissionRepository.existsByPermissionId(permissionId);

        if (usedInRole) {
            String message = "Không thể xoá end-point này vì đang được sử dụng bởi role";

            return MyApiResponse.error(
                    HttpStatus.CONFLICT,
                    message
            );
        }

        permissionRepository.delete(permission);

        return MyApiResponse.success(
                "Xóa permission thành công",
                null
        );
    }


    public MyApiResponse<RouteResponse> getRouteByPermission(String code) {

        Permission permission = permissionRepository.findByCode(code);
        if (permission == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Permission không tồn tại");
        }

        Route route = permission.getRouteEntity();
        if (route == null) {
            return MyApiResponse.error(HttpStatus.NOT_FOUND, "Permission chưa gắn route");
        }

        RouteResponse response = new RouteResponse();
        response.setRoute(route.getRoute());
        response.setDescription(route.getDescription());
        response.setStatus(route.getStatus());

        return MyApiResponse.success("Route của permission", response);
    }

    private PermissionResponse mapToResponse(Permission p) {
        PermissionResponse r = new PermissionResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setCode(p.getCode());
        r.setEndPoint(p.getRoute());
        r.setMethod(p.getMethod());
        if (p.getRouteId() != null) {
            Route route = routeRepository.findById(p.getRouteId()).orElse(null);
            if (route != null) {
                r.setRouteName(route.getRoute());
            }
        }

        return r;
    }

    public MyApiResponse<Map<String, Object>> listPermissionsByRole(
            Long roleId,
            Pageable pageable
    ) {

        Page<Object[]> page = rolePermissionRepository.findPermissionsByRole(roleId, pageable);
        List<Object[]> list = page.getContent();

        if (list.isEmpty()) {
            return MyApiResponse.error(
                    HttpStatus.NO_CONTENT,
                    "Role chưa được gán quyền nào."
            );
        }

        List<RolePermissionResponse> responses = list.stream()
                .map(row -> new RolePermissionResponse(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (String) row[5],
                        (String) row[6]
                ))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("data", responses);
        result.put("totalItems", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());

        return MyApiResponse.success(
                "Lấy danh sách quyền theo role thành công.",
                result
        );
    }



    @Transactional
    public MyApiResponse<Void> removePermissionFromRole(Long roleId, Long permissionId) {

        boolean exists = rolePermissionRepository
                .existsByRoleIdAndPermissionId(roleId, permissionId);

        if (!exists) {
            return MyApiResponse.error(
                    HttpStatus.NOT_FOUND,
                    "Role chưa được gán permission này"
            );
        }

        rolePermissionRepository
                .deleteByRoleIdAndPermissionId(roleId, permissionId);

        return MyApiResponse.success(
                "Xoá permission khỏi role thành công",
                null
        );
    }





}


