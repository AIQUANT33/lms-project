package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  FINAL EXAM → belongs to course
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    // MODULE QUIZ / ASSIGNMENT → belongs to module
    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    @Column(nullable = false)
    private String type;
    // FINAL | QUIZ | ASSIGNMENT

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String assessmentData;

    private Integer totalMarks;
    private Integer passingMarks;

    private LocalDateTime createdAt;

    public Assessment() {
        this.createdAt = LocalDateTime.now();
    }
}