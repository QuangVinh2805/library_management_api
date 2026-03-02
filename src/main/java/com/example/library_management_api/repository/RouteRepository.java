package com.example.library_management_api.repository;

import com.example.library_management_api.models.Role;
import com.example.library_management_api.models.Route;
import com.example.library_management_api.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
//    @Query(value = "SELECT * FROM route where route := route")

    @Query(value = """
            SELECT * FROM route
            """, countQuery = """
            SELECT COUNT(*) FROM route
            """, nativeQuery = true)
    Page<Route> findAllRoute(Pageable pageable);


    Route findByRoute(String route);

    Route findRouteById(Long routeId);

    @Query(
            value = """
        SELECT *
        FROM route
        WHERE route LIKE CONCAT('%', :keyword, '%')
           OR description LIKE CONCAT('%', :keyword, '%')
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM route
        WHERE route LIKE CONCAT('%', :keyword, '%')
           OR description LIKE CONCAT('%', :keyword, '%')
        """,
            nativeQuery = true
    )
    Page<Route> searchAllRoutesByName(@Param("keyword") String keyword, Pageable pageable);



}
