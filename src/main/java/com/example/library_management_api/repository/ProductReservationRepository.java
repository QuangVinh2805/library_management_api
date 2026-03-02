package com.example.library_management_api.repository;

import com.example.library_management_api.models.BorrowedProduct;
import com.example.library_management_api.models.ProductReservation;
import com.example.library_management_api.response.ProductReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductReservationRepository extends JpaRepository<ProductReservation, Long> {

    @Query(value = """
            SELECT * FROM product_reservation WHERE reader_id = :readerId""",
            countQuery = """
            SELECT COUNT(*) FROM product_reservation WHERE reader_id = :readerId
            """,
            nativeQuery = true)
    Page<ProductReservation> findAllByUser(@Param("readerId") Long readerId, Pageable pageable);


    @Query(value = """
            SELECT * FROM product_reservation""",
            countQuery = """
            SELECT COUNT(*) FROM product_reservation
            """,
            nativeQuery = true)
    Page<ProductReservation> findAllProductReservation(Pageable pageable);

    ProductReservation findProductReservationById(Long productReservationId);

    @Query(
            value = """
        SELECT pr.*
        FROM product_reservation pr
        INNER JOIN reader u ON u.id = pr.reader_id
        WHERE (
            u.name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
            OR u.user_code COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
        )
        ORDER BY pr.id DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM product_reservation pr
        INNER JOIN reader u ON u.id = pr.reader_id
        WHERE (
            u.name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
            OR u.user_code COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
        )
        """,
            nativeQuery = true
    )
    Page<ProductReservation> searchProductReservation(
            @Param("keyword") String keyword,
            Pageable pageable
    );


    @Query("""
        SELECT p FROM ProductReservation p
        WHERE (:type = 'borrow' AND p.borrowDay BETWEEN :start AND :end)
    """)
    Page<ProductReservation> searchByDateRange(
            @Param("type") String type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable
    );


    long countByReaderId(Long readerId);


    @Modifying
    @Query("""
    UPDATE ProductReservation pr
    SET pr.status = 4,
        pr.updatedAt = :now
    WHERE pr.readerId = :readerId
      AND pr.productId = :productId
      AND pr.quantity = :quantity
      AND pr.borrowDay = :borrowDay
""")
    long updateStatusWhenReturn(
            Long readerId,
            Long productId,
            Long quantity,
            LocalDate borrowDay,
            LocalDate now
    );





}
