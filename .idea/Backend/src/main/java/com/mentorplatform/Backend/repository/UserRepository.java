package com.mentorplatform.Backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mentorplatform.Backend.entity.User;
import org.springframework.stereotype.Repository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
