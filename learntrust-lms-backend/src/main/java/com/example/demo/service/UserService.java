package com.example.demo.service;
//UserService manages user authentication, registration, and role-based user operations.
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // CREATE USER (REGISTER)
    public User createUser(User user) {
        // Check if email already exists (prevents duplicate user)
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered: " + user.getEmail());
        }
        
        // Set default values if not provided
        if (user.getStatus() == null || user.getStatus().isEmpty()) {
            user.setStatus("ACTIVE");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(java.time.LocalDateTime.now());
        }

        
         /*save() is a JPA method. 
         If the user has no ID (new user), it does an INSERT.
          If it has an ID (existing user), it does an UPDATE. 
          Returns the saved entity with the auto-generated userId filled in.*/ 
        return userRepository.save(user);
    }

    // CREATE ADMIN USER
    public User createAdmin(String name, String email, String password) {
        User admin = new User();
        admin.setName(name);
        admin.setEmail(email);
        admin.setPassword(password);
        admin.setRole("ADMIN");
        admin.setStatus("ACTIVE");
        admin.setCreatedAt(LocalDateTime.now());
        return userRepository.save(admin);
    }

    //  GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //  LOGIN
    public User login(String email, String password) {

        User user = userRepository.findByEmail(email) //SELECT * FROM users WHERE email = ?
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }
}
