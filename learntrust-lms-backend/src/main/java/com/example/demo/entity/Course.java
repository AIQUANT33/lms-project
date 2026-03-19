package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String videoUrl;

    private String status;

  
    @ManyToOne //many courses can belong to one trainer 
    @JoinColumn(name = "trainer_id", nullable = false)
    @JsonIgnoreProperties({"password", "email", "walletAddress"}) //skip these fields when parsing
    private User trainer;

    @ManyToOne
    @JoinColumn(name = "approved_by_admin_id")
    @JsonIgnoreProperties({"password"})
    private User approvedByAdmin;

    private LocalDateTime createdAt;

    public Course() {
        this.createdAt = LocalDateTime.now();
        this.status = "DRAFT"; //default
    }
}