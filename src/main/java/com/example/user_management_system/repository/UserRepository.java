package com.example.user_management_system.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import com.example.user_management_system.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role <> :role")
    List<User> findAllExcludingRole(@Param("role") String role);

    List<User> findByUsernameContainingIgnoreCase(String username);
}


