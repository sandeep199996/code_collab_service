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

@RestController
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
    private JwtUtil jwtUtil; // We need this to make the VIP pass

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
        return jwtUtil.generateToken(existingUser.getEmail());
    }

    @Autowired
    private UserRepository userRepository;

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
                // If they are searching, check names, emails, and roles for matches
                userPage = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRoleContainingIgnoreCase(
                        search, search, search, paging
                );
            }

            return ResponseEntity.ok(userPage);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}