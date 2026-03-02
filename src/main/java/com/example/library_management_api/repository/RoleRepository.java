package com.example.library_management_api.repository;

import com.example.library_management_api.models.Category;
import com.example.library_management_api.models.Role;
import com.example.library_management_api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByRoleName(String roleName);

    @Query(value = """
            SELECT * FROM role
            """, countQuery = """
            SELECT COUNT(*) FROM role
            """, nativeQuery = true)
    Page<Role> findAllRole(Pageable pageable);


    @Query(value = """
            SELECT * FROM role where status = 1
            """, countQuery = """
            SELECT COUNT(*) FROM role where status = 1
            """, nativeQuery = true)
    Page<Role> findAllRoleByStatus(Pageable pageable);


    @Query(
            value = """
                SELECT *    
                FROM role
                WHERE role_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM role
                WHERE role_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                """,
            nativeQuery = true
    )
    Page<Role> searchAllRolesByName(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT r FROM Role r WHERE LOWER(r.roleName) = LOWER(:roleName)")
    Role findByRoleName(@Param("roleName") String roleName);


    @Query(value = """
select r.*
from role r
join user_role ur on ur.role_id = r.id
where ur.user_id = :userId
""", nativeQuery = true)
    List<Role> findRolesByUserId(Long userId);

    @Query(value = """
select ur.user_id, r.id, r.role_name
from user_role ur
join role r on r.id = ur.role_id
""", nativeQuery = true)
    List<Object[]> findAllUserRoles();



    List<Role> findByRoleNameIn(List<String> roleNames);





}
