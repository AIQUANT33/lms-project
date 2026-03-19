package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link quiz to module
    @Column(nullable = false)
    private Long moduleId;

    // Quiz title
    private String title;

    // Quiz type
    private String type;

    // Questions stored as JSON
    @Lob
    @Column(columnDefinition = "TEXT")
    private String assessmentData;

    // Passing score
    private Integer passingScore;

}