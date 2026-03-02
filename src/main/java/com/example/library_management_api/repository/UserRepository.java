package com.example.library_management_api.repository;

import com.example.library_management_api.models.Product;
import com.example.library_management_api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = """
            SELECT * FROM user
            """, countQuery = """
            SELECT COUNT(*) FROM user
            """, nativeQuery = true)
    Page<User> findAllUser(Pageable pageable);


    @Query(value = """
            SELECT * FROM user where status = 1 
            """, countQuery = """
            SELECT COUNT(*) FROM user where status = 1
            """, nativeQuery = true)
    Page<User> findAllUserByStatus(Pageable pageable);

    @Query(
            value = """
                SELECT u.* 
                FROM user u
                JOIN role r ON u.role_id = r.id
                WHERE r.role_name = :roleName
                """,
            countQuery = """
                SELECT COUNT(*) 
                FROM user u
                JOIN role r ON u.role_id = r.id
                WHERE r.role_name = :roleName
                """,
            nativeQuery = true
    )
    Page<User> findAllByRoleName(
            @Param("roleName") String roleName,
            Pageable pageable
    );


    // Lấy user theo token
    @Query(value = "SELECT * FROM user WHERE token = :token", nativeQuery = true)
    User findByToken(@Param("token") String token);

    // Lấy user theo userCode
    @Query(value = "SELECT * FROM user WHERE user_code = :userCode", nativeQuery = true)
    User findByUserCode(@Param("userCode") String userCode);

    // Lấy user theo email
    @Query(value = "SELECT * FROM user WHERE email = :email", nativeQuery = true)
    User findByEmail(@Param("email") String email);


    @Query(value = "SELECT * FROM user WHERE public_id = :publicId", nativeQuery = true)
    User findByPublicId(@Param("publicId") String publicId);

    @Query(value = "SELECT * FROM user WHERE id = :userId", nativeQuery = true)
    User findByUserId(@Param("userId") Long userId);


    @Query(
            value = "SELECT COUNT(*) FROM user WHERE email = :email",
            nativeQuery = true
    )
    Long countByEmail(@Param("email") String email);


    @Query(
            value = """
            SELECT *
            FROM user
            WHERE (
                name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                OR user_code COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
            )
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM user
            WHERE (
                name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                OR user_code COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
            )
            """,
            nativeQuery = true
    )
    Page<User> searchAllUser(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT u FROM User u WHERE u.resetPasswordToken = :token")
    User findByResetToken(@Param("token") String token);


    @Query("""
        select count(ur) > 0
        from UserRole ur
        where ur.userId = :userId
          and ur.roleId = 1
    """)
    boolean existsAdminRole(@Param("userId") Long userId);


    @Query("""
select u
from User u
where u.token = :token
""")
    List<User> findByTokenList(String token);






}
