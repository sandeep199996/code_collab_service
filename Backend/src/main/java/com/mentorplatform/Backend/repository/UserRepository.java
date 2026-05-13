package com.mentorplatform.Backend.repository;

import com.mentorplatform.Backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // TO Find a user exactly by email for login/auth
    User findByEmail(String email);

    //  The Search Query

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRoleContainingIgnoreCase(
            String name, String email, String role, Pageable pageable
    );
}