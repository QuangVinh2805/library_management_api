package com.example.library_management_api.repository;

import com.example.library_management_api.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    void deleteByUserIdAndRoleId(Long userId,Long roleId);
    void deleteByUserId(Long userId);

}
