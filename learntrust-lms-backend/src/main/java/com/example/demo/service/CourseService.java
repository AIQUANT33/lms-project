package com.example.demo.service;
//CourseService manages course creation, approval workflow, and visibility for different roles.

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository,
                         UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    
    // CREATE COURSE (TRAINER)
   public Course createCourse(Course course) {

        if (course.getTrainer() == null) {
            throw new RuntimeException("Trainer must be assigned");
        }

        // Always set to DRAFT - requires admin approval
        course.setStatus("DRAFT");
        
        return courseRepository.save(course);
    }

   
    // GET ALL COURSES
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

   
    // GET COURSE BY ID
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

   
    // GET TRAINER COURSES
    public List<Course> getTrainerCourses(Long trainerId) {

        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        return courseRepository.findByTrainer(trainer);
    }

   
    // GET PUBLISHED COURSES (STUDENT)
    public List<Course> getPublishedCourses() {
        return courseRepository.findByStatus("PUBLISHED");
    }

    
    // SUBMIT FOR REVIEW
    public Course submitForReview(Long courseId) {

        Course course = getCourseById(courseId);

        if (!"DRAFT".equals(course.getStatus())) {
            throw new RuntimeException("Only draft courses can be submitted");
        }

        course.setStatus("SUBMITTED");
        return courseRepository.save(course);
    }

   
    // ADMIN: GET PENDING COURSES
    public List<Course> getPendingCourses() {
        // Return both DRAFT and SUBMITTED courses for admin approval
        List<Course> pendingCourses = new java.util.ArrayList<>();
        pendingCourses.addAll(courseRepository.findByStatus("DRAFT"));
        pendingCourses.addAll(courseRepository.findByStatus("SUBMITTED"));
        return pendingCourses;
    }

    
    // ADMIN: APPROVE COURSE
   public Course approveCourse(Long courseId, Long adminId) {

        Course course = getCourseById(courseId);

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        course.setStatus("PUBLISHED");
        course.setApprovedByAdmin(admin);

        return courseRepository.save(course);
    }

    
    // ADMIN: REJECT COURSE
   
    public Course rejectCourse(Long courseId) {

        Course course = getCourseById(courseId);

        course.setStatus("REJECTED");
        return courseRepository.save(course);
    }
}