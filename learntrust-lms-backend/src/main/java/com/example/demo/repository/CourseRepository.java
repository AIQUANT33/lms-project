package com.example.demo.repository;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // trainer courses
    List<Course> findByTrainer(User trainer);

    // by status
    List<Course> findByStatus(String status);

    //  trainer + status (very useful)
    List<Course> findByTrainerAndStatus(User trainer, String status);

    long countByStatus(String status);
}