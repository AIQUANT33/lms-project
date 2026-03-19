package com.example.demo.repository;

import com.example.demo.entity.TrainerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrainerRequestRepository
        extends JpaRepository<TrainerRequest, Long> {

        List<TrainerRequest> findByStatus(String status);    
        
        
        long countByStatus(String status);

}