package com.example.demo.repository;

import com.example.demo.entity.Submission;
import com.example.demo.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByQuiz(Quiz quiz);

}