package com.example.library_management_api.repository;

import com.example.library_management_api.models.BorrowedProduct;
import com.example.library_management_api.models.ProductReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowedProductRepository extends JpaRepository<BorrowedProduct, Long> {
    @Query(value = "SELECT * FROM borrowed_product WHERE reader_id = :readerId", nativeQuery = true)
    List<BorrowedProduct> findByReaderId(@Param("readerId") Long readerId);

    @Query(value = """ 
SELECT * FROM borrowed_product WHERE reader_id = :readerId""",
            countQuery = """ 
SELECT COUNT(*) FROM borrowed_product WHERE reader_id = :readerId
"""
            , nativeQuery = true)
    Page<BorrowedProduct> findAllByReaderId(@Param("readerId") Long readerId, Pageable pageable);

    List<BorrowedProduct> findByReaderIdAndProductIdAndQuantityAndBorrowDate(
            Long readerId,
            Long productId,
            Long quantity,
            LocalDate borrowDate
    );

    @Query(value = "SELECT * FROM borrowed_product WHERE return_date IS NULL AND due_date < CURRENT_DATE", nativeQuery = true)
    List<BorrowedProduct> findAllOverdue(); // Tìm tất cả sách đang quá hạn

    @Query(value = "SELECT COUNT(*) FROM borrowed_product WHERE reader_id = :readerId AND is_lost = 1", nativeQuery = true)
    Long countLost(@Param("readerId") Long readerId);

    @Query(value = "SELECT COUNT(*) FROM borrowed_product WHERE reader_id = :readerId AND return_date > due_date", nativeQuery = true)
    Long countOverdueReturns(@Param("readerId") Long readerId);

    @Query(value = "SELECT * FROM borrowed_product WHERE id = :borrowedProductId", nativeQuery = true)
    BorrowedProduct findBorrowedProductById(@Param("borrowedProductId") Long borrowedProductId);


    @Query(value = """ 
            SELECT * FROM borrowed_product 
            """, countQuery = """ 
            SELECT COUNT(*)  FROM borrowed_product
            """, nativeQuery = true)
    Page<BorrowedProduct> findAllBorrowedProducts(Pageable pageable);


    @Query(value = """
            SELECT 
                MONTH(bp.borrow_date) AS month,
                COUNT(*) AS borrowCount
            FROM borrowed_product bp
            WHERE YEAR(bp.borrow_date) = :year
            GROUP BY MONTH(bp.borrow_date)
            ORDER BY MONTH(bp.borrow_date)
            """, nativeQuery = true)
    List<Object[]> getBorrowCountByMonth(@Param("year") int year);



    @Query(
            value = """
        SELECT bp.*
        FROM borrowed_product bp
        INNER JOIN reader u ON u.id = bp.reader_id
        WHERE (
            u.email COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
        )
        ORDER BY bp.id DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM borrowed_product bp
        INNER JOIN reader u ON u.id = bp.reader_id
        WHERE (
            u.email COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
        )
        """,
            nativeQuery = true
    )
    Page<BorrowedProduct> searchBorrowedProduct(
            @Param("keyword") String keyword,
            Pageable pageable
    );


    @Query("""
        SELECT b FROM BorrowedProduct b
        WHERE 
            (:type = 'borrow' AND b.borrowDate BETWEEN :start AND :end)
            OR (:type = 'return' AND b.returnDate BETWEEN :start AND :end)
            OR (:type = 'due' AND b.dueDate BETWEEN :start AND :end)
    """)
    Page<BorrowedProduct> searchByDateRange(
            @Param("type") String type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable
    );


    long countByReaderId(Long readerId);


    @Query(value = "SELECT * FROM borrowed_product",
            countQuery = "SELECT COUNT(*) FROM borrowed_product",
            nativeQuery = true)
    Page<BorrowedProduct> findAllBorrowedProduct(Pageable pageable);

    @Query(value = """
        SELECT COUNT(*)
        FROM borrowed_product
        WHERE reader_id = :readerId
        AND status = 3
        AND return_date IS NULL
        AND due_date IS NOT NULL
        AND CURDATE() > DATE_ADD(due_date, INTERVAL 7 DAY)
        """, nativeQuery = true)
    long countOverdueMoreThan7Days(Long readerId);

    @Query(value = """
        SELECT COUNT(*)
        FROM borrowed_product
        WHERE reader_id = :readerId
        AND status = 3
        AND return_date IS NULL
        """, nativeQuery = true)
    long countCurrentBorrowing(Long readerId);


    @Query("""
       SELECT COUNT(bp)
       FROM BorrowedProduct bp
       WHERE bp.readerId = :readerId
       AND (bp.status = 1 or bp.status = 2 or bp.status = 3)
       """)
    long countPendingReservation(Long readerId);


    @Query("""
       SELECT COALESCE(SUM(bp.quantity),0)
       FROM BorrowedProduct bp
       WHERE bp.readerId = :readerId
       AND bp.status = 3
       """)
    long countBorrowingBooks(Long readerId);





}
