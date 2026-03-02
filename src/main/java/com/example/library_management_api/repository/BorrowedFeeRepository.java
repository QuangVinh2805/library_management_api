package com.example.library_management_api.repository;

import com.example.library_management_api.models.BorrowedFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowedFeeRepository extends JpaRepository<BorrowedFee, Long> {
    @Query(value = "SELECT * FROM borrowed_fee WHERE borrowed_product_id = :borrowedProductId", nativeQuery = true)
    BorrowedFee findByBorrowedProductId(@Param("borrowedProductId") Long borrowedProductId);


}
