package com.foodhub.repository;

import com.foodhub.entity.User;
import com.foodhub.enums.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    @Query("select count(u) from User u join u.roles r where r.name = :roleName")
    long countByRole(@Param("roleName") RoleName roleName);
}
