package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.dto.ProgressAnalytics;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentProgressService {

    private final EnrollmentRepository enrollmentRepository;
    private final ModuleCompletionRepository moduleCompletionRepository;
    private final ContentCompletionRepository contentCompletionRepository;
    private final AssessmentSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public StudentProgressService(
            EnrollmentRepository enrollmentRepository,
            ModuleCompletionRepository moduleCompletionRepository,
            ContentCompletionRepository contentCompletionRepository,
            AssessmentSubmissionRepository submissionRepository,
            UserRepository userRepository) {

        this.enrollmentRepository = enrollmentRepository;
        this.moduleCompletionRepository = moduleCompletionRepository;
        this.contentCompletionRepository = contentCompletionRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // AI SUMMARY DATA
    // =====================================================

    public String buildProgressData(Long studentId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByStudent(student);

        int totalCourses = enrollments.size();
        int completedCourses = 0;

        for (Enrollment e : enrollments) {
            if ("COMPLETED".equals(e.getCompletionStatus())) {
                completedCourses++;
            }
        }

        long totalModulesCompleted = 0;
        long totalLessonsCompleted = 0;

        for (Enrollment e : enrollments) {

            Course course = e.getCourse();

            totalModulesCompleted +=
                    moduleCompletionRepository.countByStudentAndModule_Course(student, course);

            totalLessonsCompleted +=
                    contentCompletionRepository.countByStudentAndContent_Module_Course(student, course);
        }

        Double avgScore =
                submissionRepository.findAverageScoreByStudent(student);

        if (avgScore == null) avgScore = 0.0;

        return
                "Student learning data:\n" +
                "Courses enrolled: " + totalCourses + "\n" +
                "Courses completed: " + completedCourses + "\n" +
                "Modules completed: " + totalModulesCompleted + "\n" +
                "Lessons completed: " + totalLessonsCompleted + "\n" +
                "Average quiz score: " + avgScore;
    }

    // =====================================================
    // WEEKLY STUDY ACTIVITY
    // =====================================================

    private int[] getWeeklyStudyActivity(User student) {

        int[] weekly = new int[7]; // Mon-Sun

        List<ContentCompletion> completions =
                contentCompletionRepository.findByStudent(student);

        for (ContentCompletion completion : completions) {

            int dayIndex =
                    completion.getCompletedAt()
                            .getDayOfWeek()
                            .getValue() - 1;

            weekly[dayIndex] += 1;
        }

        return weekly;
    }

    // =====================================================
    // ANALYTICS FOR DASHBOARD
    // =====================================================

    public ProgressAnalytics getStudentAnalytics(Long studentId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Enrollment> enrollments =
                enrollmentRepository.findByStudent(student);

        int totalCourses = enrollments.size();
        int completedCourses = 0;

        for (Enrollment e : enrollments) {
            if ("COMPLETED".equals(e.getCompletionStatus())) {
                completedCourses++;
            }
        }

        int activeCourses = totalCourses - completedCourses;

        long modulesCompleted = 0;
        long lessonsCompleted = 0;

        for (Enrollment e : enrollments) {

            Course course = e.getCourse();

            modulesCompleted +=
                    moduleCompletionRepository
                            .countByStudentAndModule_Course(student, course);

            lessonsCompleted +=
                    contentCompletionRepository
                            .countByStudentAndContent_Module_Course(student, course);
        }

        Double avgScore =
                submissionRepository.findAverageScoreByStudent(student);

        if (avgScore == null) avgScore = 0.0;

        int[] weeklyActivity = getWeeklyStudyActivity(student);

        return new ProgressAnalytics(
                totalCourses,
                completedCourses,
                activeCourses,
                modulesCompleted,
                lessonsCompleted,
                avgScore,
                weeklyActivity
        );
    }
}