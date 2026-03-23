package com.example.demo.service;

import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.TrainerRequestRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service //Spring creates one singleton instance of this class 
public class AdminStatsService {
    //this service needs to query three different tables (users, courses, trainer_requests) to build the full stats picture.
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final TrainerRequestRepository trainerRequestRepository;

    //Spring sees this constructor, finds the three matching beans it already manages, and injects them automatically.
    public AdminStatsService(UserRepository userRepository,
                             CourseRepository courseRepository,
                             TrainerRequestRepository trainerRequestRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.trainerRequestRepository = trainerRequestRepository;
    }



    public Map<String, Object> getPlatformStats() {

        Map<String, Object> stats = new HashMap<>();

        // users(checks how many users have the roles of students/trainer/admin)
        stats.put("totalStudents", userRepository.countByRole("STUDENT"));
        stats.put("totalTrainers", userRepository.countByRole("TRAINER"));
        stats.put("totalAdmins", userRepository.countByRole("ADMIN"));

        // courses
        stats.put("totalCourses", courseRepository.count());
        stats.put("pendingCourses", courseRepository.countByStatus("SUBMITTED"));

        // trainer requests
        stats.put("pendingTrainerRequests",
                trainerRequestRepository.countByStatus("PENDING"));

        return stats;
    }
}