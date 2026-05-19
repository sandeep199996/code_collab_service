package com.mentorplatform.Backend.controller;
import com.mentorplatform.Backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.mentorplatform.Backend.repository.UserRepository;
import com.mentorplatform.Backend.service.UserService;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.mentorplatform.Backend.util.JwtUtil;
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

    @GetMapping("/all")
    public List   <User> getAllUsers() {
        return userService.getAllUsers();
    }
}