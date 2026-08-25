package com.mentorplatform.Backend.controller;

import com.mentorplatform.Backend.entity.CodingChallenge;
import com.mentorplatform.Backend.repository.CodingChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class CodingChallengeController {

    @Autowired
    private CodingChallengeRepository challengeRepository;

    @GetMapping
    public ResponseEntity<List<CodingChallenge>> getAvailableChallenges(Principal principal) {
        String userEmail = principal.getName();
        return ResponseEntity.ok(challengeRepository.findAvailableChallengesForUser(userEmail));
    }

    @PostMapping("/create")
    public ResponseEntity<CodingChallenge> createChallenge(@RequestBody CodingChallenge challenge, Principal principal) {
        challenge.setAuthorEmail(principal.getName());
        challenge.setPublic(false);
        return ResponseEntity.ok(challengeRepository.save(challenge));
    }
    @PutMapping("/{id}/publish")
    public ResponseEntity<String> publishChallenge(@PathVariable Long id, Principal principal) {
        CodingChallenge challenge = challengeRepository.findById(id).orElseThrow();

        if (challenge.getAuthorEmail().equals(principal.getName())) {
            challenge.setPublic(true);
            challengeRepository.save(challenge);
            return ResponseEntity.ok("Challenge is now public!");
        }
        return ResponseEntity.status(403).body("Unauthorized");
    }


}