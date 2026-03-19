package com.example.demo.controller;

import com.example.demo.entity.AssessmentSubmission;
import com.example.demo.service.AssessmentSubmissionService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assessment-submissions")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:44313"})
public class AssessmentSubmissionController {

    private final AssessmentSubmissionService submissionService;

    // Constructor Injection
    public AssessmentSubmissionController(
            AssessmentSubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    /*
        STUDENT: Submit assessment

        POST /assessment-submissions?assessmentId=1&studentId=1

        Body:
        {
            "answers": [...]
        }

        Note:
        submissionData is stored as JSON string
    */
    @PostMapping
    public AssessmentSubmission submitAssessment(
            @RequestParam Long assessmentId,
            @RequestParam Long studentId,
            @RequestBody String submissionData) {

        return submissionService.submitAssessment(
                assessmentId,
                studentId,
                submissionData
        );
    }

    /*
        TRAINER: Evaluate assignment manually

        PUT /assessment-submissions/evaluate/{submissionId}?score=15
    */
    @PutMapping("/evaluate/{submissionId}")
    public AssessmentSubmission evaluateSubmission(
            @PathVariable Long submissionId,
            @RequestParam int score) {

        return submissionService.evaluateSubmission(
                submissionId,
                score
        );
    }
}
