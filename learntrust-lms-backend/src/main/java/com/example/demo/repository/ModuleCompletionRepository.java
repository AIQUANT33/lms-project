package com.example.demo.repository;

import com.example.demo.entity.ModuleCompletion;
import com.example.demo.entity.User;
import com.example.demo.entity.Module;
import com.example.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface ModuleCompletionRepository extends JpaRepository<ModuleCompletion, Long> {

    Optional<ModuleCompletion> findByStudentAndModule(User student, Module module);

    long countByStudentAndModule_Course(User student, Course course);

    @Modifying
    @Transactional
    void deleteByModule(Module module); 
}