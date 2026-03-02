package com.example.library_management_api.repository;

import com.example.library_management_api.models.BlackList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Long> {
    @Query(value = "SELECT * FROM black_list WHERE reader_id = :readerId", nativeQuery = true)
    BlackList findByReaderId(@Param("readerId") Long readerId);


    @Query(value = """ 
            SELECT * FROM black_list """,
            countQuery = """ 
                    SELECT COUNT(*) FROM black_list
                    """, nativeQuery = true)
    Page<BlackList> findAllBlackList(Pageable pageable);


    boolean existsByReaderId(Long readerId);

    @Query(
            value = """
        SELECT b.*
        FROM black_list b
        INNER JOIN reader u ON u.id = b.reader_id
        WHERE (u.name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
               OR u.user_code COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%'))
    """,
            countQuery = """
        SELECT COUNT(*)
        FROM black_list b
        INNER JOIN reader u ON u.id = b.reader_id
        WHERE (u.name COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%')
               OR u.user_code COLLATE utf8mb4_bin LIKE CONCAT('%', :keyword, '%'))
    """,
            nativeQuery = true
    )
    Page<BlackList> searchBlacklistedReader(@Param("keyword") String keyword, Pageable pageable);






}
