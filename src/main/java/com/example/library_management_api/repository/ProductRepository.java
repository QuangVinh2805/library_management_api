package com.example.library_management_api.repository;

import com.example.library_management_api.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """ 
            SELECT * FROM product
            """, countQuery = """
            SELECT  COUNT(*) FROM product
            """, nativeQuery = true)
    Page<Product> findAllProducts(Pageable pageable);


    @Query(value = """ 
            SELECT * FROM product WHERE status = 1
            """, countQuery = """
            SELECT  COUNT(*) FROM product WHERE status = 1
            """, nativeQuery = true)
    Page<Product> findAllProductsByStatus(Pageable pageable);

    // Lấy sản phẩm theo hashId
    @Query(value = "SELECT * FROM product WHERE hash_id = :hashId", nativeQuery = true)
    Product findByHashId(@Param("hashId") String hashId);


    @Query(value = "SELECT * FROM product WHERE id = :productId", nativeQuery = true)
    Product findByProductId(@Param("productId") Long productId);

    @Query(value = """
            SELECT * FROM product WHERE is_new = 1 AND status = 1
            """
            , countQuery = """
            SELECT COUNT(*) FROM product WHERE is_new = 1 AND status = 1
            """,
            nativeQuery = true)
    Page<Product> findNewProduct(Pageable pageable);

    @Modifying
    @Query(value = """
    UPDATE product
    SET is_new = 0
    WHERE is_new = 1 AND DATEDIFF(NOW(), created_at) >= 30
""", nativeQuery = true)
    void updateExpiredNewProducts();




    @Query(value = """
            SELECT p.*
            FROM product p
            JOIN product_category pc ON p.id = pc.product_id
            WHERE pc.category_id = :categoryId
              AND p.status = 1
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM product p
                    JOIN product_category pc ON p.id = pc.product_id
                    WHERE pc.category_id = :categoryId
                      AND p.status = 1
                    """,
            nativeQuery = true)
    Page<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);


    @Query(value = """
            SELECT p.*
            FROM product p
            JOIN product_author pa ON p.id = pa.product_id
            WHERE pa.author_id = :authorId
              AND p.status = 1
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM product p
                    JOIN product_author pa ON p.id = pa.product_id
                    WHERE pc.author_id = :authorId
                      AND p.status = 1
                    """,
            nativeQuery = true)
    Page<Product> findProductsByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    @Query(
            value = """
                SELECT *
                FROM product
                WHERE product_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                AND status = 1
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM product
                WHERE product_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                AND status = 1
                """,
            nativeQuery = true
    )
    Page<Product> searchProductsByNameAndStatus(@Param("keyword") String keyword, Pageable pageable);

    @Query(
            value = """
                SELECT *
                FROM product
                WHERE product_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM product
                WHERE product_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                """,
            nativeQuery = true
    )
    Page<Product> searchAllProductsByName(@Param("keyword") String keyword, Pageable pageable);




    @Query(value = """
            SELECT * 
            FROM product 
            WHERE publisher_id = :publisherId AND status = 1
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM product 
                    WHERE publisher_id = :publisherId AND status = 1
                    """,
            nativeQuery = true)
    Page<Product> findByPublisherId(@Param("publisherId") Long publisherId, Pageable pageable);


    @Query(
            value = "SELECT COUNT(pr.id) " +
                    "FROM publisher p " +
                    "LEFT JOIN product pr " +
                    "ON pr.publisher_id = p.id AND pr.status = 1 " +
                    "WHERE p.id = :publisherId",
            nativeQuery = true
    )
    Long countBooksByPublisherId(@Param("publisherId") Long publisherId);


    @Query(value = """
            SELECT p.* 
            FROM product p
            JOIN favourite_product fp ON p.id = fp.product_id
            JOIN reader u ON u.id = fp.reader_id
            WHERE u.token = :token AND fp.status = 1
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM product p
                    JOIN favourite_product fp ON p.id = fp.product_id
                    JOIN reader u ON u.id = fp.reader_id
                    WHERE u.token = :token AND fp.status = 1
                    """,
            nativeQuery = true)
    Page<Product> findFavouriteProductsByUserToken(@Param("token") String token, Pageable pageable);


    @Query(value = """
            SELECT p.* 
            FROM product p
            LEFT JOIN favourite_product f ON p.id = f.product_id AND f.status = 1
            GROUP BY p.id
            ORDER BY COUNT(f.id) DESC
            """,
            countQuery = """
                    SELECT COUNT(*) 
                    FROM product
                    """,
            nativeQuery = true)
    Page<Product> findTopFavouriteProducts(Pageable pageable);


    @Query(value = """
            SELECT p.* 
            FROM product p
            INNER JOIN product_reservation r ON p.id = r.product_id AND r.status IN (3,4)
            GROUP BY p.id
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT p.id)
                    FROM product p
                    INNER JOIN product_reservation r ON p.id = r.product_id AND r.status IN (3,4)
                    """,
            nativeQuery = true)
    Page<Product> findTopBorrowedProducts(Pageable pageable);


    @Query(value = """
            SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS total
            FROM product
            GROUP BY DATE_FORMAT(created_at, '%Y-%m')
            ORDER BY month DESC
            """, nativeQuery = true)
    List<Map<String, Object>> countProductsByMonth();


}
