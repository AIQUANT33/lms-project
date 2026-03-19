package com.example.demo.controller;

import com.example.demo.dto.AssessmentRequest;
import com.example.demo.dto.AssessmentSubmissionRequest;
import com.example.demo.entity.Assessment;
import com.example.demo.entity.AssessmentAttempt;
import com.example.demo.entity.Module;
import com.example.demo.repository.AssessmentRepository;
import com.example.demo.repository.ModuleRepository;
import com.example.demo.service.AssessmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assessments")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:44313"})
public class AssessmentController {

    private final AssessmentService assessmentService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    //  constructor
    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    /*
     * CREATE QUIZ / ASSIGNMENT
     */
    @PostMapping
    public Assessment createAssessment(@RequestBody AssessmentRequest request) {
        return assessmentService.createAssessment(request);
    }

    /*
     * SUBMIT QUIZ / ASSIGNMENT
     */
    @PostMapping("/submit")
    public AssessmentAttempt submitAssessment(
            @RequestBody AssessmentSubmissionRequest request) {

        return assessmentService.submitAssessment(request);
    }

    /*
     * GET QUIZZES BY MODULE (VERY IMPORTANT)
     */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<Assessment>> getByModule(
            @PathVariable Long moduleId) {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        List<Assessment> quizzes = assessmentRepository.findByModule(module);

        return ResponseEntity.ok(quizzes);
    }
@DeleteMapping("/{assessmentId}")
public void deleteAssessment(@PathVariable Long assessmentId) {
    assessmentService.deleteAssessment(assessmentId);
}

}