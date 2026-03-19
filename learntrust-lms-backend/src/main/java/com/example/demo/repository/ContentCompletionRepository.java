package com.example.demo.repository;

import com.example.demo.entity.ContentCompletion;
import com.example.demo.entity.ModuleContent;
import com.example.demo.entity.Module;
import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface ContentCompletionRepository
        extends JpaRepository<ContentCompletion, Long> {

    Optional<ContentCompletion> findByStudentAndContent(User student, ModuleContent content);

    long countByStudentAndContent_Module(User student, Module module);

    long countByStudentAndContent_Module_Course(User student, Course course);

    List<ContentCompletion> findByStudent(User student);

    @Modifying
    @Transactional
    void deleteByContent(ModuleContent content); 
}