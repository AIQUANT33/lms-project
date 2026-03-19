package com.example.demo.repository;

import com.example.demo.entity.Module;
import com.example.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByCourseOrderByModuleOrderAsc(Course course);

    long countByCourse(Course course);

    boolean existsByCourseAndModuleOrder(Course course, int moduleOrder);
}

/*

Automatically generates SQL like:

SELECT * FROM modules
WHERE course_id = ?
ORDER BY module_order ASC;



*/