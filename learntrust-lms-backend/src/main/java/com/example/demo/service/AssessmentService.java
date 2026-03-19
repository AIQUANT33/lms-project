package com.example.demo.service;

import com.example.demo.dto.AssessmentRequest;
import com.example.demo.dto.AssessmentSubmissionRequest;

import com.example.demo.entity.Assessment;
import com.example.demo.entity.AssessmentAttempt;
import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.Module; 
import com.example.demo.entity.User;

import com.example.demo.repository.AssessmentRepository;
import com.example.demo.repository.AssessmentAttemptRepository;
import com.example.demo.repository.ContentCompletionRepository;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.ModuleContentRepository;
import com.example.demo.repository.ModuleRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModuleContentRepository moduleContentRepository;
    private final ContentCompletionRepository contentCompletionRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository; 

    public AssessmentService(
            AssessmentRepository assessmentRepository,
            AssessmentAttemptRepository assessmentAttemptRepository,
            UserRepository userRepository,
            EnrollmentRepository enrollmentRepository,
            ModuleContentRepository moduleContentRepository,
            ContentCompletionRepository contentCompletionRepository,
            CourseRepository courseRepository,
            ModuleRepository moduleRepository 
    ) {
        this.assessmentRepository = assessmentRepository;
        this.assessmentAttemptRepository = assessmentAttemptRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.moduleContentRepository = moduleContentRepository;
        this.contentCompletionRepository = contentCompletionRepository;
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository; 
    }

    // create assesment (quiz)
    public Assessment createAssessment(AssessmentRequest request) {

        Assessment assessment = new Assessment();

        // FINAL EXAM
        if ("FINAL".equalsIgnoreCase(request.getType())) {

            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            assessment.setCourse(course);
        }

        //  MODULE QUIZ
        else if ("QUIZ".equalsIgnoreCase(request.getType())) {

            Module module = moduleRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new RuntimeException("Module not found"));

            assessment.setModule(module);
        }

        else {
            throw new RuntimeException("Invalid assessment type");
        }

        assessment.setTitle(request.getTitle());
        assessment.setType(request.getType());
        assessment.setAssessmentData(request.getQuestionsJson());
        assessment.setPassingMarks(request.getPassingScore());
        assessment.setTotalMarks(100);

        return assessmentRepository.save(assessment);
    }

    // submit final assesment
    public AssessmentAttempt submitAssessment(AssessmentSubmissionRequest request) {

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        AssessmentAttempt attempt = assessmentAttemptRepository
                .findByStudentAndAssessment(student, assessment)
                .orElse(new AssessmentAttempt());

        attempt.setStudent(student);
        attempt.setAssessment(assessment);
        attempt.setScore(request.getScore());

        if (assessment.getPassingMarks() != null &&
            request.getScore() >= assessment.getPassingMarks()) {

            attempt.setStatus("PASSED");
            completeCourseIfEligible(student, assessment.getCourse());

        } else {
            attempt.setStatus("FAILED");
        }

        return assessmentAttemptRepository.save(attempt);
    }

    //course completion
    private void completeCourseIfEligible(User student, Course course) {

        long totalContents =
                moduleContentRepository.countByModule_Course(course);

        long completedContents =
                contentCompletionRepository
                        .countByStudentAndContent_Module_Course(student, course);

        if (totalContents == 0) return;

        int progress =
                (int) ((completedContents * 100) / totalContents);

        Enrollment enrollment =
                enrollmentRepository
                        .findByStudentAndCourse(student, course)
                        .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setProgressPercentage(progress);

        if (progress == 100) {
            enrollment.setCompletionStatus("COMPLETED");
        }

        enrollmentRepository.save(enrollment);
    }


    public void deleteAssessment(Long assessmentId) {
    assessmentRepository.deleteById(assessmentId);
}

    
}














