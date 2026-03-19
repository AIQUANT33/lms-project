package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


/* 
 stores:
 student
 assessment
 submitted answers (json)
 score
 status (PENDING / PASSED / FAILED)
 submittedAt
*/


@Data
@Entity
@Table(name = "assessment_submissions")
public class AssessmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Student answers JSON
    @Column(columnDefinition = "TEXT", nullable = false)
    private String submissionData;

    private int score;

    // PENDING / PASSED / FAILED
    private String status;

    private LocalDateTime submittedAt;

    public AssessmentSubmission() {
        this.submittedAt = LocalDateTime.now();
        this.score = 0;
        this.status = "PENDING";
    }
}


// stores the raw answers a student submitted, starts as PENDING, trainer reviews it