package com.example.demo.service;

import com.example.demo.dto.ContentCompletionRequest;
import com.example.demo.entity.ContentCompletion;
import com.example.demo.entity.Course;
import com.example.demo.entity.Enrollment;
import com.example.demo.entity.Module;
import com.example.demo.entity.ModuleCompletion;
import com.example.demo.entity.ModuleContent;
import com.example.demo.entity.User;

import com.example.demo.repository.AssessmentRepository;
import com.example.demo.repository.ContentCompletionRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.ModuleCompletionRepository;
import com.example.demo.repository.ModuleContentRepository;
import com.example.demo.repository.ModuleRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class ContentCompletionService {

    private final ContentCompletionRepository contentCompletionRepository;
    private final ModuleContentRepository moduleContentRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleCompletionRepository moduleCompletionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;


    // Constructor Injection
    public ContentCompletionService(
            ContentCompletionRepository contentCompletionRepository,
            ModuleContentRepository moduleContentRepository,
            ModuleRepository moduleRepository,
            ModuleCompletionRepository moduleCompletionRepository,
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            AssessmentRepository assessmentRepository) {

        this.contentCompletionRepository = contentCompletionRepository;
        this.moduleContentRepository = moduleContentRepository;
        this.moduleRepository = moduleRepository;
        this.moduleCompletionRepository = moduleCompletionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;

    }


    /*
      CONTENT COMPLETION LOGIC
     
     Steps:
      1. Validate student
      2. Validate content
      3. Prevent duplicate completion
      4. Save completion
      5. Update course progress
      6. Check module completion
     */
    public ContentCompletion completeContent(ContentCompletionRequest request) {

        // Step 1: Fetch student
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Step 2: Fetch content
        ModuleContent content = moduleContentRepository.findById(request.getContentId())
                .orElseThrow(() -> new RuntimeException("Content not found"));

        // Step 3: Prevent duplicate content completion
        contentCompletionRepository.findByStudentAndContent(student, content)
                .ifPresent(existing -> {
                    throw new RuntimeException("Content already completed");
                });

        // Step 4: Save new completion record
        ContentCompletion completion = new ContentCompletion();
        completion.setStudent(student);
        completion.setContent(content);

        contentCompletionRepository.save(completion);

        // Step 5: Update overall course progress immediately
        updateEnrollmentProgress(student, content.getModule().getCourse());

        // Step 6: Check if module is fully completed
        checkAndCompleteModule(student, content.getModule());

        return completion;
    }


    /*
      MODULE COMPLETION LOGIC
     
      Module is completed only if:
       All contents complete
       AND no assessment exists
     
      If assessment exists → completion will be handled after passing assessment
     */
    private void checkAndCompleteModule(User student, Module module) {

        // Count total contents in module
        long totalContents = moduleContentRepository.countByModule(module);

        // Count completed contents in module
        long completedContents = contentCompletionRepository
                .countByStudentAndContent_Module(student, module);

        // If no content exists, do nothing
        if (totalContents == 0) {
            return;
        }

        // If all contents completed
        if (totalContents == completedContents) {

            // Check if module has assessment
            boolean hasAssessment = assessmentRepository.existsByModule(module);

            // If NO assessment → complete module immediately
            if (!hasAssessment) {

                moduleCompletionRepository
                        .findByStudentAndModule(student, module)
                        .orElseGet(() -> {

                            ModuleCompletion moduleCompletion = new ModuleCompletion();
                            moduleCompletion.setStudent(student);
                            moduleCompletion.setModule(module);

                            ModuleCompletion saved =
                                    moduleCompletionRepository.save(moduleCompletion);

                            // Update progress again after module completion
                            updateEnrollmentProgress(student, module.getCourse());

                            return saved;
                        });
            }

            // If assessment exists → wait for assessment pass logic
        }
    }


    /*
      COURSE PROGRESS CALCULATION
      Progress = (Completed Contents in Course / Total Contents in Course) * 100
      Partial progress is supported.
      100% marks enrollment as COMPLETED.
     */
    private void updateEnrollmentProgress(User student, Course course) {

        // Count total contents in entire course
        long totalContents =
                moduleContentRepository.countByModule_Course(course);

        // Count completed contents in entire course
        long completedContents =
                contentCompletionRepository
                        .countByStudentAndContent_Module_Course(student, course);

        // Fetch enrollment record
        Enrollment enrollment =
                enrollmentRepository
                        .findByStudentAndCourse(student, course)
                        .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        // If no content exists
        if (totalContents == 0) {
            enrollment.setProgressPercentage(0);
        } else {

            int progress =
                    (int) ((completedContents * 100) / totalContents);

            enrollment.setProgressPercentage(progress);

            if (progress == 100) {
                enrollment.setCompletionStatus("COMPLETED");
            }
        }

        enrollmentRepository.save(enrollment);
    }
}
