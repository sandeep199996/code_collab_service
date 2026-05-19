package com.mentorplatform.Backend.controller;


import com.mentorplatform.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // TO Inject  WebSocket presence service here later to get live active connections!
    //@Autowired
    // private PresenceService presenceService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // 1. Database Metrics
            long totalUsers = userRepository.count();
            // Assuming your User entity has a getRole() method
            long totalMentors = userRepository.findAll().stream()
                    .filter(u -> "MENTOR".equalsIgnoreCase(u.getRole().name()))
                    .count();
            long totalMentees = userRepository.findAll().stream()
                    .filter(u -> "MENTEE".equalsIgnoreCase(u.getRole().name()))
                    .count();

            stats.put("totalUsers", totalUsers);
            stats.put("totalMentors", totalMentors);
            stats.put("totalMentees", totalMentees);

            // 2. Live WebSocket Metrics (Mocked for now)
            stats.put("activeWebSockets", 42); // Example static number
            stats.put("liveEncryptedTunnels", 12); // Example static number
            stats.put("serverStatus", "OPERATIONAL");

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            stats.put("serverStatus", "DEGRADED");
            stats.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(stats);
        }
    }
}