package com.example.demo.controller;

import com.example.demo.entity.Enrollment;
import com.example.demo.service.EnrollmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@CrossOrigin
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public Enrollment enrollStudent(@RequestParam Long studentId,
                                    @RequestParam Long courseId) {
        return enrollmentService.enrollStudent(studentId, courseId);
    }

    @GetMapping("/my-courses")
    public List<Enrollment> getMyCourses(@RequestParam Long studentId) {
        return enrollmentService.getEnrollmentsByStudent(studentId);
    }

    @GetMapping
    public List<Enrollment> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }

    @GetMapping("/student/{id}")
    public List<Enrollment> getEnrollmentsByStudent(@PathVariable Long id) {
        return enrollmentService.getEnrollmentsByStudent(id);
    }

    @GetMapping("/progress")
    public Enrollment getEnrollmentProgress(@RequestParam Long studentId,
                                            @RequestParam Long courseId) {
        return enrollmentService.getEnrollmentByStudentAndCourse(studentId, courseId);
    }

    //  STUDENT REQUEST
    @PutMapping("/complete")
    public Enrollment markCourseComplete(@RequestParam Long studentId,
                                         @RequestParam Long courseId) {
        return enrollmentService.markCourseComplete(studentId, courseId);
    }

    //  TRAINER APPROVE
    @PutMapping("/approve/{id}")
    public Enrollment approve(@PathVariable Long id) {
        return enrollmentService.approveCertificate(id);
    }

    @GetMapping("/trainer/{id}")
public List<Enrollment> getByTrainer(@PathVariable Long id) {
    return enrollmentService.getByTrainer(id);
}
}