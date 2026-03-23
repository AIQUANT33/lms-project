package com.example.demo.service;

import com.example.demo.entity.TrainerRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.TrainerRequestRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerRequestService {

    private final TrainerRequestRepository repository;
    private final UserRepository userRepository;

    public TrainerRequestService(TrainerRequestRepository repository,
                                 UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    
    // CREATE TRAINER REQUEST
   public TrainerRequest createRequest(TrainerRequest request) {

    String email = request.getEmail().trim().toLowerCase();

    //  check user exists
    userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() ->
                    new RuntimeException("User must register before applying as trainer"));

    // Check if a pending request already exists for this email
    List<TrainerRequest> existingRequests = repository.findByStatus("PENDING");
    for (TrainerRequest existing : existingRequests) {
        if (existing.getEmail() != null && existing.getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("A pending trainer request already exists for this email");
        }
    }

    request.setEmail(email);
    request.setStatus("PENDING");

    return repository.save(request);
}

   
    // ADMIN — GET PENDING REQUESTS
    public List<TrainerRequest> getPendingRequests() {
        return repository.findByStatus("PENDING");
    }

    // ADMIN — APPROVE REQUEST 
 public TrainerRequest approveRequest(Long requestId) {

    TrainerRequest request = repository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));

    //  update request status
    request.setStatus("APPROVED");
    //  upgrade user role 
    Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

    if (userOpt.isPresent()) {
        User user = userOpt.get();
        user.setRole("TRAINER");
        userRepository.save(user);
    }

    return repository.save(request);
}

    // ADMIN — REJECT REQUEST
    public TrainerRequest rejectRequest(Long requestId) {

        TrainerRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("REJECTED");

        return repository.save(request);
    }
}