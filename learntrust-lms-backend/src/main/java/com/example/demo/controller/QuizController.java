package com.example.demo.controller;

import com.example.demo.entity.Quiz;
import com.example.demo.service.QuizService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@CrossOrigin(origins = "http://localhost:4200")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService){
        this.quizService = quizService;
    }

    @PostMapping
    public Quiz createQuiz(@RequestBody Quiz quiz){
        return quizService.saveQuiz(quiz);
    }

    @GetMapping("/module/{moduleId}")
    public List<Quiz> getModuleQuizzes(@PathVariable Long moduleId){
        return quizService.getModuleQuizzes(moduleId);
    }

    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id){
        quizService.deleteQuiz(id);
    }
}