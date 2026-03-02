package com.example.library_management_api.repository;

import com.example.library_management_api.models.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Long> {

    // Lấy danh sách category theo product_id
    @Query(value = "SELECT * FROM product_category WHERE product_id = :productId", nativeQuery = true)
    List<ProductCategory> findByProductId(@Param("productId") Long productId);

    // Lấy danh sách sản phẩm theo category_id
    @Query(value = "SELECT * FROM product_category WHERE category_id = :categoryId", nativeQuery = true)
    List<ProductCategory> findByCategoryId(@Param("categoryId") Long categoryId);

    void deleteByProductId(Long productId);

    boolean existsByProductIdAndCategoryId(Long productId, Long categoryId);

    @Query(value = "SELECT COUNT(*) FROM product p " +
            "JOIN product_category pc ON p.id = pc.product_id " +
            "WHERE pc.category_id = :categoryId AND p.status = 1", nativeQuery = true)
    int countProductsByCategoryId(@Param("categoryId") Long categoryId);
}
