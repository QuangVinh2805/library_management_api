package com.example.library_management_api.repository;

import com.example.library_management_api.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.lang.model.util.SimpleElementVisitor6;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query(value = "SELECT * FROM notification WHERE id = :notificationId", nativeQuery = true)
    Notification findByNotificationId(@Param("notificationId") Long notificationId);


    @Query(value = """
             SELECT * FROM notification 
            """, countQuery = """ 
            SELECT * FROM notification
            """, nativeQuery = true)
    Page<Notification> findAllNotification(Pageable pageable);
}
