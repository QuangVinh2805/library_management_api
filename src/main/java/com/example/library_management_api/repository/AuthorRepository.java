package com.example.library_management_api.repository;


import com.example.library_management_api.models.Author;
import com.example.library_management_api.models.Product;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Author findAuthorById(Long authorId);

    // Tìm theo tên tác giả
    @Query(value = "SELECT * FROM author WHERE author_name = :authorName AND status = 1 LIMIT 1", nativeQuery = true)
    Author findByAuthorName(@Param("authorName") String authorName);


    //Lấy danh sách author qua productId
    @Query(value = "SELECT a.* FROM author a " +
            "JOIN product_author pa ON a.id = pa.author_id " +
            "WHERE pa.product_id = :productId AND a.status = 1", nativeQuery = true)
    List<Author> findAuthorsByProductId(@Param("productId") Long productId);


    @Query(value = """ 
            SELECT * FROM author
            """, countQuery = """
            SELECT COUNT(*) FROM author""", nativeQuery = true)
    Page<Author> findAllAuthors(Pageable pageable);


    @Query(value = """ 
            SELECT * FROM author WHERE status = 1 
            """, countQuery = """
            SELECT COUNT(*) FROM author WHERE status = 1  """, nativeQuery = true)
    Page<Author> findAllAuthorsByStatus(Pageable pageable);


    @Query(
            value = """
                SELECT *
                FROM author
                WHERE author_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM author
                WHERE author_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                """,
            nativeQuery = true
    )
    Page<Author> searchAllAuthorsByName(@Param("keyword") String keyword, Pageable pageable);


}
