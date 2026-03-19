package com.example.demo.controller;

import com.example.demo.entity.TrainerRequest;
import com.example.demo.service.TrainerRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainer-requests")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:44313"})
public class TrainerRequestController {

    private final TrainerRequestService service;

    public TrainerRequestController(TrainerRequestService service) {
        this.service = service;
    }

    // =====================================================
    // CREATE REQUEST (STUDENT → TRAINER APPLICATION)
    // =====================================================
    @PostMapping
    public TrainerRequest createRequest(@RequestBody TrainerRequest request) {
        try {
            return service.createRequest(request);
        } catch (RuntimeException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    // =====================================================
    // ADMIN — VIEW PENDING REQUESTS
    // =====================================================
    @GetMapping("/pending")
    public List<TrainerRequest> getPendingRequests() {
        return service.getPendingRequests();
    }

    // =====================================================
    // ADMIN — APPROVE REQUEST 
    // =====================================================
    @PutMapping("/{requestId}/approve")
    public TrainerRequest approveRequest(@PathVariable Long requestId) {
        return service.approveRequest(requestId);
    }

    // =====================================================
    // ADMIN — REJECT REQUEST
    // =====================================================
    @PutMapping("/{requestId}/reject")
    public TrainerRequest rejectRequest(@PathVariable Long requestId) {
        return service.rejectRequest(requestId);
    }
}