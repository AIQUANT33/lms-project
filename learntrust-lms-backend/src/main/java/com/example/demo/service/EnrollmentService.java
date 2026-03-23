package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    //First it finds the student, then checks their role. 
    // This prevents a trainer or admin from enrolling in a course using this endpoint. 
    //validations before enrolling : student;published;no duplicate enrollment
    public Enrollment enrollStudent(Long studentId, Long courseId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!student.getRole().equals("STUDENT")) {
            throw new RuntimeException("Only students can enroll");
        }

      //Finds the course then checks it's PUBLISHED.
      //  A student cannot enroll in a DRAFT or SUBMITTED course. 
      Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!"PUBLISHED".equals(course.getStatus())) {
            throw new RuntimeException("Course is not available");
        }


        //prevent duplicate enrollment
        enrollmentRepository.findByStudentAndCourse(student, course)
                .ifPresent(e -> {
                    throw new RuntimeException("Already enrolled");
                });
        //create enrollment
        // link the student and course

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        return enrollmentRepository.save(enrollment);
    }



    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return enrollmentRepository.findByStudent(student);
    }

    public Enrollment getEnrollmentByStudentAndCourse(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return enrollmentRepository.findByStudentAndCourse(student, course)
                .orElse(null);
    }

    //  STUDENT REQUEST
    //This sets progress to 100 and status to PENDING_APPROVAL. 
    // The enrollment goes into a waiting state — the trainer must now review and approve. 
    //orElse(null) : creates new enrollment if it doesn't exist (defensive coding)
    public Enrollment markCourseComplete(Long studentId, Long courseId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = enrollmentRepository
                .findByStudentAndCourse(student, course)
                .orElse(null);

        if (enrollment == null) {
            enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
        }

        enrollment.setProgressPercentage(100);
        enrollment.setCompletionStatus("PENDING_APPROVAL");

        return enrollmentRepository.save(enrollment);
    }

    
    public List<Enrollment> getByTrainer(Long trainerId) {
    return enrollmentRepository.findByCourseTrainerUserId(trainerId);
}
    //  TRAINER APPROVE
  public Enrollment approveCertificate(Long id) {

    Enrollment e = enrollmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));

    //  only allow approval if pending
    if (!"PENDING_APPROVAL".equals(e.getCompletionStatus())) {
        throw new RuntimeException("Certificate is not in pending state");
    }

    e.setCompletionStatus("APPROVED");

    return enrollmentRepository.save(e);
}
}