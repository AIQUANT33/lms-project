package com.example.demo.repository;

import com.example.demo.entity.Assessment;
import com.example.demo.entity.Module;
import com.example.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    //  MODULE QUIZ 
    List<Assessment> findByModule(Module module);

    boolean existsByModule(Module module);

    //  FINAL COURSE ASSESSMENT 
    List<Assessment> findByCourse(Course course);


    
}