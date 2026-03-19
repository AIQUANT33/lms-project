package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;

@Service
public class AssessmentSubmissionService {

    /* 
    handles
    submit logic
    auto evaluation
    mark pending for assignment
    trigger module completion check
     */

    private final AssessmentRepository assessmentRepository;
    private final AssessmentSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public AssessmentSubmissionService(
            AssessmentRepository assessmentRepository,
            AssessmentSubmissionRepository submissionRepository,
            UserRepository userRepository) {

        this.assessmentRepository = assessmentRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    // Student submits assessment
    public AssessmentSubmission submitAssessment(
            Long assessmentId,
            Long studentId,
            String submissionData) {

        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        submissionRepository.findByAssessmentAndStudent(assessment, student)
                .ifPresent(s -> {
                    throw new RuntimeException("Already submitted");
                });

        AssessmentSubmission submission = new AssessmentSubmission();
        submission.setAssessment(assessment);
        submission.setStudent(student);
        submission.setSubmissionData(submissionData);

        // Auto evaluate if QUIZ
       if (assessment.getType().equals("QUIZ")) {

    submission.setScore(assessment.getTotalMarks());
    submission.setStatus("PASSED");

} else {

    submission.setStatus("PENDING"); // trainer review required
}

        return submissionRepository.save(submission);
    }

    // Trainer manually evaluates assignment
    public AssessmentSubmission evaluateSubmission(
            Long submissionId,
            int score) {

        AssessmentSubmission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setScore(score);

        if (score >= submission.getAssessment().getPassingMarks()) {
            submission.setStatus("PASSED");
        } else {
            submission.setStatus("FAILED");
        }

        return submissionRepository.save(submission);
    }
}



//handles raw answer submission, auto-passes quizzes, marks assignments PENDING for trainer review