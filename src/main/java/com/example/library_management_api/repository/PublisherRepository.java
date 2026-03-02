package com.example.library_management_api.repository;

import com.example.library_management_api.models.Author;
import com.example.library_management_api.models.Product;
import com.example.library_management_api.models.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.concurrent.Flow;

public interface PublisherRepository extends JpaRepository<Publisher,Long> {
    Publisher findPublisherById(Long publisherId);

    // Tìm theo tên nhà xuất bản
    @Query(value = "SELECT * FROM publisher WHERE publisher_name = :publisherName LIMIT 1", nativeQuery = true)
    Publisher findByPublisherName(@Param("publisherName") String publisherName);


    @Query(value = """ 
            SELECT * FROM publisher 
            """, countQuery = """
            SELECT COUNT(*) FROM publisher """, nativeQuery = true)
    Page<Publisher> findAllPublishers(Pageable pageable);


    @Query(value = """ 
            SELECT * FROM publisher WHERE status = 1 
            """, countQuery = """
            SELECT COUNT(*) FROM publisher WHERE status = 1  """, nativeQuery = true)
    Page<Publisher> findAllPublishersByStatus(Pageable pageable);


    @Query(
            value = """
                SELECT *
                FROM publisher
                WHERE publisher_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM publisher
                WHERE publisher_name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
                """,
            nativeQuery = true
    )
    Page<Publisher> searchAllPublishersByName(@Param("keyword") String keyword, Pageable pageable);
}
