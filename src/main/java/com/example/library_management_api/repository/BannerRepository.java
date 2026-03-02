package com.example.library_management_api.repository;

import com.example.library_management_api.models.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    @Query(value = "SELECT * FROM banner WHERE id = :bannerId", nativeQuery = true)
    Banner findByBannerId(@Param("bannerId") Long bannerId);

}
