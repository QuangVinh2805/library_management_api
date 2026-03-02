package com.example.library_management_api.repository;

import com.example.library_management_api.models.FavouriteProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavouriteProductRepository extends JpaRepository<FavouriteProduct, Long> {
    @Query(value = "SELECT COUNT(*) FROM favourite_product WHERE product_id = :productId AND status = 1", nativeQuery = true)
    Long countByProductId(@Param("productId") Long productId);

    @Query(value = "SELECT * FROM favourite_product WHERE reader_id = :readerId AND product_id = :productId LIMIT 1", nativeQuery = true)
    FavouriteProduct findFavourite(@Param("readerId") Long readerId, @Param("productId") Long productId);


    @Query(value = "SELECT * FROM favourite_product " +
            "WHERE reader_id = :readerId AND product_id = :productId AND status = 1 LIMIT 1",
            nativeQuery = true)
    FavouriteProduct findActiveByUserAndProduct(@Param("readerId") Long readerId,
                                                @Param("productId") Long productId);


    @Query(value = "SELECT COUNT(*) FROM favourite_product WHERE product_id = :productId AND status = 1", nativeQuery = true)
    List<FavouriteProduct> findFavouriteProductByProductId(@Param("productId") Long productId);


    FavouriteProduct findFavouriteProductById(Long id);


    long countByReaderId(Long readerId);



}
