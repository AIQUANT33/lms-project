package com.example.demo.repository;

import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudent(User student);

    Optional<Enrollment> findByStudentAndCourse(User student, Course course);

    List<Enrollment> findByCourseTrainerUserId(Long trainerId);

    long countByCourse(Course course);

    @Modifying
    @Transactional
    void deleteByCourse(Course course); 
}