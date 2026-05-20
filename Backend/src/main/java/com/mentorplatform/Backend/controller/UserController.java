package com.mentorplatform.Backend.controller;
import com.mentorplatform.Backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mentorplatform.Backend.repository.UserRepository;
import com.mentorplatform.Backend.service.UserService;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.mentorplatform.Backend.util.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.security.Principal;
import java.util.Map;

import javax.management.relation.Relation;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
    @RequestMapping("/api/users")
    public class UserController {
@Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        // Registration logic will go here
        return userService.registerUser(user);
}
    @Autowired
    private PasswordEncoder passwordEncoder; // We need this to check the hash

    @Autowired
    private JwtUtil jwtUtil; // We need this to make the VIP pass (JWT) after successful login

    @Autowired
    private UserRepository userRepository; // We need this to fetch the user's role for the JWT
    // POST API for Logging In

    @PostMapping("/login")
    public String loginUser(@RequestBody User loginRequest) {
        // 1. Find the user in the database
        User existingUser = userService.getAllUsers().stream()
                .filter(u -> u.getEmail().equals(loginRequest.getEmail()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // 2. Check if the plain-text password matches the hashed password in the DB
        if (!passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        // 3. If everything is correct, generate and return the VIP pass (JWT)
        // Pass the role we fetched from the database directly into the token generator!

        // THE FIX: Use 'existingUser' instead of 'user'
        String token = jwtUtil.generateToken(existingUser.getEmail(), existingUser.getRole().name());

        return token;
    }


    @GetMapping("/directory")
    public ResponseEntity<Page<User>> getPaginatedDirectory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {

        try {
            // Create the pagination request (e.g., Page 0, show 10 items)
            Pageable paging = PageRequest.of(page, size);
            Page<User> userPage;

            // If there is no search term, return a standard chunk of the database
            if (search == null || search.trim().isEmpty()) {
                userPage = userRepository.findAll(paging);
            } else {
                //  Calling custom query!
                userPage = userRepository.searchUsers(search, paging);
            }

            return ResponseEntity.ok(userPage);

        } catch (Exception e) {
            // Print the exact error to the backend console so we don't fly blind!
            System.err.println("Search crash: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyAccount(Principal principal) {
        try {            // 1. IDENTIFY THE USER
            // The 'Principal' object is automatically injected by Spring Security.
            // to reads the email directly from the validated JWT, impossible to forge.
            String email = principal.getName();

            // 2. FETCH THE USER
            User userToDelete = userRepository.findByEmail(email);

            if (userToDelete == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User footprint not found."));
            }

            // 3. DELETE ASSOCIATED DATA
            //  deleted  here first before deleting the user object to avoid SQL foreign key errors!
            // snippetRepository.deleteAllByUser(userToDelete);

            // 4. THE NUCLEAR OPTION
            userRepository.delete(userToDelete);

            // 5. CONFIRMATION
            return ResponseEntity.ok(Map.of("message", "User " + email + " and all associated data have been permanently erased."));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to wipe data: " + e.getMessage()));
        }
    }
}