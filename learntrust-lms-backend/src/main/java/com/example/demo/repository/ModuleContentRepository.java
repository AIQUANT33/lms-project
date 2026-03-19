package com.example.demo.repository;

import com.example.demo.entity.ModuleContent;
import com.example.demo.entity.Module;
import com.example.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleContentRepository extends JpaRepository<ModuleContent, Long> {

    List<ModuleContent> findByModuleOrderBySequenceOrderAsc(Module module);

    long countByModule(Module module);

    long countByModule_Course(Course course);

    List<ModuleContent> findByModule(Module module); 
}