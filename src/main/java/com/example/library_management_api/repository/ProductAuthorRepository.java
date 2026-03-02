package com.example.library_management_api.repository;

import com.example.library_management_api.models.ProductAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAuthorRepository extends JpaRepository<ProductAuthor,Long> {
    // Lấy danh sách author theo product_id
    @Query(value = "SELECT * FROM product_author WHERE product_id = :productId", nativeQuery = true)
    List<ProductAuthor> findByProductId(@Param("productId") Long productId);

    // Lấy danh sách sản phẩm theo author_id
    @Query(value = "SELECT * FROM product_author WHERE author_id = :authorId", nativeQuery = true)
    List<ProductAuthor> findByAuthorId(@Param("authorId") Long authorId);

    void deleteByProductId(Long productId);

    boolean existsByProductIdAndAuthorId(Long productId, Long authorId);

    @Query(value = "SELECT COUNT(*) FROM product p " +
            "JOIN product_author pa ON p.id = pa.product_id " +
            "WHERE pa.author_id = :authorId AND p.status = 1", nativeQuery = true)
    int countProductsByAuthorId(@Param("authorId") Long authorId);

}
