package com.example.library_management_api.repository;

import com.example.library_management_api.models.RolePermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    @Query("""
        SELECT COUNT(rp) > 0
        FROM UserRole ur
        JOIN RolePermission rp ON ur.roleId = rp.roleId
        WHERE ur.userId = :userId
        AND rp.permissionId = :permissionId
    """)
    boolean hasPermission(Long userId, Long permissionId);


    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    @Query(
            value = """
            SELECT 
                p.id,
                r.role_name,
                p.name,
                p.route,
                p.method,
                rt.route,
                rt.description
            FROM role_permission rp
            JOIN role r ON rp.role_id = r.id
            JOIN permission p ON rp.permission_id = p.id
            LEFT JOIN route rt ON p.route_id = rt.id
            WHERE r.id = :roleId
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM role_permission rp
            WHERE rp.role_id = :roleId
            """,
            nativeQuery = true
    )
    Page<Object[]> findPermissionsByRole(
            @Param("roleId") Long roleId,
            Pageable pageable
    );


    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);

    boolean existsByPermissionId(Long permissionId);


    @Query("""
       select rp.permissionId
       from RolePermission rp
       where rp.roleId = :roleId
       and rp.permissionId in :permissionIds
       """)
    List<Long> findPermissionIdsByRoleIdAndPermissionIdIn(
            Long roleId,
            List<Long> permissionIds
    );

    @Query(
            value = """
        SELECT 
            p.id,
            r.role_name,
            p.name,
            p.route,
            p.method,
            rt.route,
            rt.description
        FROM role_permission rp
        JOIN role r ON rp.role_id = r.id
        JOIN permission p ON rp.permission_id = p.id
        LEFT JOIN route rt ON p.route_id = rt.id
        WHERE r.id = :roleId
        AND (:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%'))
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM role_permission rp
        JOIN permission p ON rp.permission_id = p.id
        WHERE rp.role_id = :roleId
        AND (:keyword IS NULL OR p.name LIKE CONCAT('%', :keyword, '%'))
        """,
            nativeQuery = true
    )
    Page<Object[]> findPermissionByRole(
            @Param("roleId") Long roleId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}
