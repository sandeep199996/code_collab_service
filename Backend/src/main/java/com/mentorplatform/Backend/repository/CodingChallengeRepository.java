package com.mentorplatform.Backend.repository;

import com.mentorplatform.Backend.entity.CodingChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface CodingChallengeRepository extends JpaRepository<CodingChallenge, Long> {
    @Query("SELECT c FROM CodingChallenge c WHERE c.isPublic = true OR c.authorEmail = :userEmail")
    List<CodingChallenge> findAvailableChallengesForUser(@Param("userEmail") String userEmail);
}