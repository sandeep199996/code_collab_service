package com.mentorplatform.Backend.repository;

import com.mentorplatform.Backend.entity.Snippet;
import com.mentorplatform.Backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {


    List<Snippet> findByUserOrderByCreatedAtDesc(User user);


    void deleteAllByUser(User user);
}