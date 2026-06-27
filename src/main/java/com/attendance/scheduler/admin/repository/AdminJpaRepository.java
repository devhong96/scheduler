package com.attendance.scheduler.admin.repository;

import com.attendance.scheduler.admin.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminJpaRepository extends JpaRepository<Admin, Long> {

    Admin findByUsernameIs(String username);
    boolean existsByEmail(String email);
}
