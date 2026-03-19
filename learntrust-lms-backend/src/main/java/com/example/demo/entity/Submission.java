package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private String reviewStatus;
    // PENDING, APPROVED, REJECTED

    @Column(nullable = false)
    private LocalDate submittedDate;

    public Submission() {
    }

    public Submission(Quiz quiz, User student) {
        this.quiz = quiz;
        this.student = student;
        this.reviewStatus = "PENDING";
        this.submittedDate = LocalDate.now();
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public User getStudent() {
        return student;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}
