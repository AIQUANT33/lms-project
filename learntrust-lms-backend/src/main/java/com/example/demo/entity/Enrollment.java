package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"password"})
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"approvedByAdmin"})
    private Course course;

    private int progressPercentage;

    private String completionStatus;

    private LocalDateTime enrolledAt;

    public Enrollment() {
        this.enrolledAt = LocalDateTime.now();
        this.progressPercentage = 0;
        this.completionStatus = "IN_PROGRESS";
    }
}