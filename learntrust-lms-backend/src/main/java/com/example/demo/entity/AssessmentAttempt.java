package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "assessment_attempts")
public class AssessmentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    private int score;

    private String status; // PASSED or FAILED

    private LocalDateTime attemptedAt;

    public AssessmentAttempt() {
        this.attemptedAt = LocalDateTime.now();
    }
}


//records a student's attempt at an assessment — their score and PASSED/FAILED