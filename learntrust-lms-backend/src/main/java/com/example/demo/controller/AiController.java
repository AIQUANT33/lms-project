package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public String askAI(@RequestBody AiRequest request) {
        return aiService.askAI(request.getQuestion());
    }

    @PostMapping("/generate-quiz")
    public String generateQuiz(@RequestBody QuizGenerationRequest request) {
        return aiService.generateQuiz(
                request.getContent(),
                request.getNumQuestions()
        );
    }

    @PostMapping("/progress-summary")
    public String generateSummary(
            @RequestBody ProgressSummaryRequest request) {

        return aiService.generateProgressSummary(
                request.getProgressData()
        );
    }
}