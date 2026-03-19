package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "content_completions")
public class ContentCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private ModuleContent content;

    private LocalDateTime completedAt;

    public ContentCompletion() {
        this.completedAt = LocalDateTime.now();
    }
}
