package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = {"http://localhost:4200"})
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET ALL USERS
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // TOGGLE ACTIVE / INACTIVE
    @PutMapping("/{id}/toggle-status")
    public User toggleUserStatus(@PathVariable Long id) {

        User user = userRepository.findById(id).orElseThrow();

        if (user.getStatus().equals("ACTIVE")) {
            user.setStatus("INACTIVE");
        } else {
            user.setStatus("ACTIVE");
        }

        return userRepository.save(user);
    }

    // UPDATE ROLE
    @PutMapping("/{id}/role")
    public User updateRole(@PathVariable Long id,
                           @RequestParam String role) {

        User user = userRepository.findById(id).orElseThrow();
        user.setRole(role);

        return userRepository.save(user);
    }
}