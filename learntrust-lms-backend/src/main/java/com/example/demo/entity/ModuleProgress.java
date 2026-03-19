package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "module_progress")
public class ModuleProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long progressId;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false)
    private String status;
    // COMPLETED, NOT_COMPLETED

    public ModuleProgress() {
    }

    public ModuleProgress(Enrollment enrollment, Module module) {
        this.enrollment = enrollment;
        this.module = module;
        this.status = "NOT_COMPLETED";
    }

    public Long getProgressId() {
        return progressId;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public Module getModule() {
        return module;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
