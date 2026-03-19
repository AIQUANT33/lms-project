package com.example.demo.repository;

import com.example.demo.entity.Assessment;
import com.example.demo.entity.AssessmentSubmission;
import com.example.demo.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface AssessmentSubmissionRepository
        extends JpaRepository<AssessmentSubmission, Long> {

    Optional<AssessmentSubmission> findByAssessmentAndStudent(
            Assessment assessment, User student);

    List<AssessmentSubmission> findByStudent(User student);

    @Query("SELECT COALESCE(AVG(a.score),0) FROM AssessmentSubmission a WHERE a.student = :student")
    Double findAverageScoreByStudent(@Param("student") User student);
}