package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trainer_requests")
public class TrainerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String expertise;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String status; // PENDING, APPROVED, REJECTED

    private LocalDateTime createdAt;

    public TrainerRequest() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
}