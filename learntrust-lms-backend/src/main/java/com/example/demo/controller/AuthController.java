package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:44313"})
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> payload) {

        String email    = payload.get("email");
        String password = payload.get("password");

        // Validate credentials (existing logic unchanged)
        User user = userService.login(email, password);

        // Generate JWT token
        String token = jwtUtil.generateToken(
            user.getEmail(),
            user.getRole(),
            user.getUserId()
        );

        // Return token + user info (NO password)
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("status", user.getStatus());
        // password is intentionally NOT included

        return response;
    }
}