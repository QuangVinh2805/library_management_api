package com.example.library_management_api.repository;

import com.example.library_management_api.models.Author;
import com.example.library_management_api.models.Category;
import com.example.library_management_api.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Lấy category theo id
    Category findCategoryById(Long id);

    // Tìm theo tên thể loại
    @Query(value = "SELECT * FROM category WHERE category_name = :categoryName LIMIT 1", nativeQuery = true)
    Category findByCategoryName(@Param("categoryName") String categoryName);


    //Lấy danh sách category qua productId
    @Query(value = "SELECT c.* FROM category c " +
            "JOIN product_category pc ON c.id = pc.category_id " +
            "WHERE pc.product_id = :productId", nativeQuery = true)
    List<Category> findCategoriesByProductId(@Param("productId") Long productId);


    @Query(value = """ 
            SELECT * FROM category
            """, countQuery = """
            SELECT COUNT(*) FROM category """, nativeQuery = true)
    Page<Category> findAllCategories(Pageable pageable);

    @Query(value = """ 
            SELECT * FROM category WHERE status = 1 
            """, countQuery = """
            SELECT COUNT(*) FROM category WHERE status = 1  """, nativeQuery = true)
    Page<Category> findAllCategoriesByStatus(Pageable pageable);

    @Query(
            value = """
                SELECT *
                FROM category
                WHERE category_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM category
                WHERE category_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                """,
            nativeQuery = true
    )
    Page<Category> searchAllCategoriesByName(@Param("keyword") String keyword, Pageable pageable);


}
