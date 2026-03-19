package com.example.demo.controller;

import com.example.demo.service.AdminStatsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:44313"})
public class AdminController {

    private final AdminStatsService adminStatsService;

    public AdminController(AdminStatsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return adminStatsService.getPlatformStats();
    }
}