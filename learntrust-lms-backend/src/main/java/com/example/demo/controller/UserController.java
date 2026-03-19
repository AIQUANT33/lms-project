package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.service.StudentProgressService;
import com.example.demo.service.AiService;
import com.example.demo.dto.ProgressAnalytics;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:44313"})
public class UserController {

    private final UserService userService;
    private final StudentProgressService progressService;
    private final AiService aiService;

    public UserController(
            UserService userService,
            StudentProgressService progressService,
            AiService aiService) {

        this.userService = userService;
        this.progressService = progressService;
        this.aiService = aiService;
    }

    // ================= CREATE USER =================

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // ================= CREATE ADMIN =================

    @PostMapping("/admin")
    public User createAdmin(@RequestBody Map<String, String> payload) {

        String name = payload.get("name");
        String email = payload.get("email");
        String password = payload.get("password");

        return userService.createAdmin(name, email, password);
    }

    // ================= TEST ADMIN =================

    @GetMapping("/create-admin")
    public String createAdminTest(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {

        User admin = userService.createAdmin(name, email, password);

        return "Admin created successfully! ID: "
                + admin.getUserId()
                + ", Email: "
                + admin.getEmail();
    }

    // ================= GET USERS =================

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // =================================================
    // AI STUDENT PROGRESS SUMMARY
    // =================================================

    @GetMapping("/{id}/ai-progress-summary")
    public String getStudentAISummary(@PathVariable Long id) {

        String progressData = progressService.buildProgressData(id);

        return aiService.generateProgressSummary(progressData);
    }

    // =================================================
    // STUDENT ANALYTICS
    // =================================================

    @GetMapping("/{id}/analytics")
    public ProgressAnalytics getStudentAnalytics(@PathVariable Long id) {

        return progressService.getStudentAnalytics(id);
    }
}