package com.example.demo.repository;

import com.example.demo.entity.AssessmentAttempt;
import com.example.demo.entity.Assessment;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt, Long> {

    Optional<AssessmentAttempt> findByStudentAndAssessment(User student, Assessment assessment);

    long countByStudentAndAssessment_Module(User student, com.example.demo.entity.Module module);
}
