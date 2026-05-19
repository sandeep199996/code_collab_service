package com.mentorplatform.Backend.service;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.mentorplatform.Backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.mentorplatform.Backend.entity.User;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public User registerUser(User user) {
        // Registration logic will go here (e.g., validation, password hashing)
        User existingUser = userRepository.findByEmail(user.getEmail()) ;
        if (existingUser != null)    {
            throw new IllegalArgumentException("A user with this Email is already in use");}
        // <-- Hash the password before saving! -->
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }
}
