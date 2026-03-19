package com.example.demo.service;

import com.example.demo.entity.Quiz;
import com.example.demo.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository){
        this.quizRepository = quizRepository;
    }

    public Quiz saveQuiz(Quiz quiz){
        return quizRepository.save(quiz);
    }

    public List<Quiz> getModuleQuizzes(Long moduleId){
        return quizRepository.findByModuleIdOrderByIdAsc(moduleId);
    }

    public void deleteQuiz(Long id){
        quizRepository.deleteById(id);
    }
}