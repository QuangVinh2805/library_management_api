package com.example.library_management_api.repository;

import com.example.library_management_api.models.Permission;
import com.example.library_management_api.models.User;
import com.example.library_management_api.response.RouteLinkResponse;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {


//    @Query("""
//select p
//from Permission p
//join RolePermission rp on rp.permissionId = p.id
//join UserRole ur on rp.roleId = ur.roleId
//where ur.id = :userId
//""")
//    List<Permission> findPermissionsByUser(Long userId);

    @Query(value = """
select p.*
from permission p
join role_permission rp on rp.permission_id = p.id
join user_role ur on ur.role_id = rp.role_id
where ur.user_id = :userId
""", nativeQuery = true)
    List<Permission> findPermissionsByUser(Long userId);



    @Query("""
select p
from Permission p
join RolePermission rp on rp.permissionId = p.id
join Reader r on r.roleId = rp.roleId
where r.id = :readerId
""")
    List<Permission> findPermissionsByReader(Long readerId);


    Permission findByCode(String code);

    List<Permission> findByRouteId(Long routeId);


    @Query("""
    SELECT new com.example.library_management_api.response.RouteLinkResponse(
        r.route,
        p.route,
        p.method
    )
    FROM RolePermission rp
    JOIN Permission p ON rp.permissionId = p.id
    JOIN Route r ON p.routeId = r.id
    WHERE rp.roleId = :roleId
""")
    List<RouteLinkResponse> findRouteLinksByRoleId(@Param("roleId") Long roleId);


    @Query(value = """
select distinct
       r.route       as route,
       p.route       as endPoint,
       p.method      as method
from role_permission rp
join permission p on p.id = rp.permission_id
join route r on r.id = p.route_id
where rp.role_id in (:roleIds)
""", nativeQuery = true)
    List<RouteLinkResponse> findRouteLinksByRoleIds(List<Long> roleIds);




    @Query(value = """
            SELECT * FROM permission
            """, countQuery = """
            SELECT COUNT(*) FROM permission
            """, nativeQuery = true)
    Page<Permission> findAllPermission(Pageable pageable);


    @Query(
            value = """
        SELECT p.*
        FROM permission p
        LEFT JOIN route r ON p.route_id = r.id
        WHERE
            (
                p.name   COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                OR p.code   COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                OR p.method COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                OR p.route  COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                OR r.route  COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
            )
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM permission p
        LEFT JOIN route r ON p.route_id = r.id
        WHERE
            (
                p.name   COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                OR p.code   COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                OR p.method COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                OR p.route  COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                OR r.route  COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
            )
        """,
            nativeQuery = true
    )
    Page<Permission> searchAllPermissions(
            @Param("keyword") String keyword,
            Pageable pageable
    );




}
