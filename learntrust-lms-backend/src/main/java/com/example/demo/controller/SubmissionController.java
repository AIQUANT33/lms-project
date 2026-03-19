package com.example.demo.controller;

import com.example.demo.entity.Submission;
import com.example.demo.entity.Quiz;
import com.example.demo.repository.SubmissionRepository;
import com.example.demo.repository.QuizRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submissions")
@CrossOrigin(origins = "http://localhost:4200")
public class SubmissionController {

    private final SubmissionRepository submissionRepo;
    private final QuizRepository quizRepo;

    public SubmissionController(SubmissionRepository submissionRepo,
                                QuizRepository quizRepo) {
        this.submissionRepo = submissionRepo;
        this.quizRepo = quizRepo;
    }

    // GET submissions by quiz
    @GetMapping("/quiz/{quizId}")
    public List<Submission> getSubmissions(@PathVariable Long quizId) {
        Quiz quiz = quizRepo.findById(quizId).orElseThrow();
        return submissionRepo.findByQuiz(quiz);
    }

    // REVIEW submission
    @PutMapping("/{id}/review")
    public Submission reviewSubmission(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        Submission sub = submissionRepo.findById(id).orElseThrow();
        sub.setReviewStatus(status);
        return submissionRepo.save(sub);
    }
}