package com.example.library_management_api.repository;

import com.example.library_management_api.models.Reader;
import com.example.library_management_api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    @Query(value = "SELECT * FROM reader WHERE email = :email", nativeQuery = true)
    Reader findByEmail(@Param("email") String email);


    @Query(value = "SELECT * FROM reader WHERE public_id = :publicId", nativeQuery = true)
    Reader findByPublicId(@Param("publicId") String publicId);


    @Query(value = """
            SELECT * FROM reader
            """, countQuery = """
            SELECT COUNT(*) FROM reader
            """, nativeQuery = true)
    Page<Reader> findAllReader(Pageable pageable);


    @Query(value = "SELECT * FROM reader WHERE token = :token", nativeQuery = true)
    Reader findByToken(@Param("token") String token);


    @Query(value = "SELECT * FROM reader WHERE id = :readerId", nativeQuery = true)
    Reader findByReaderId(@Param("readerId") Long readerId);


    @Query(
            value = """
        SELECT *
        FROM reader
        WHERE (
            (name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
            OR user_code COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%'))
            AND role_id = 3
        )
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM reader
        WHERE (
            (name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
            OR user_code COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%'))
            AND role_id = 3
        )
        """,
            nativeQuery = true
    )
    Page<Reader> searchAllReader(@Param("keyword") String keyword, Pageable pageable);


    @Query(
            value = "SELECT COUNT(*) FROM reader WHERE email = :email",
            nativeQuery = true
    )
    Long countByEmail(@Param("email") String email);


    @Query("SELECT r FROM Reader r WHERE r.resetPasswordToken = :token")
    Reader findByResetToken(@Param("token") String token);

    @Query("""
select r
from Reader r
where r.token = :token
""")
    List<Reader> findByTokenList(String token);



}
