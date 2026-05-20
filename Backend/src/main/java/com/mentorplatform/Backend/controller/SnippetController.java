package com.mentorplatform.Backend.controller;

import com.mentorplatform.Backend.entity.Snippet;
import com.mentorplatform.Backend.entity.User;
import com.mentorplatform.Backend.repository.SnippetRepository;
import com.mentorplatform.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/snippets")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class SnippetController {

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. SAVE A NEW SNIPPET
    @PostMapping
    public ResponseEntity<?> saveSnippet(@RequestBody Snippet snippetRequest, Principal principal) {
        try {
            User owner = userRepository.findByEmail(principal.getName());
            if (owner == null) return ResponseEntity.status(404).body("User not found");

            // TO Lock the snippet to the authenticated user
            snippetRequest.setUser(owner);
            Snippet savedSnippet = snippetRepository.save(snippetRequest);

            return ResponseEntity.ok(savedSnippet);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 2.TO GET  SNIPPET LIBRARY
    @GetMapping
    public ResponseEntity<List<Snippet>> getMySnippets(Principal principal) {
        User owner = userRepository.findByEmail(principal.getName());

        // Fetch only the snippets belonging to this user, newest first
        List<Snippet> myLibrary = snippetRepository.findByUserOrderByCreatedAtDesc(owner);
        return ResponseEntity.ok(myLibrary);
    }

    // 3. TO DELETE A SNIPPET
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSnippet(@PathVariable Long id, Principal principal) {
        try {
            Snippet snippet = snippetRepository.findById(id).orElse(null);

            if (snippet == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Snippet not found"));
            }

            // Ensure the authenticated user is the owner of the snippet before allowing deletion
            if (!snippet.getUser().getEmail().equals(principal.getName())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access Denied. You do not own this snippet."));
            }

            snippetRepository.delete(snippet);
            return ResponseEntity.ok(Map.of("message", "Snippet deleted successfully."));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}