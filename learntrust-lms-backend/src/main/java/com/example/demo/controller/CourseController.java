package com.example.demo.controller;

import com.example.demo.dto.CourseDetailsDTO;
import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;

//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.demo.entity.Module;
import com.example.demo.service.ModuleService;

@RestController
@RequestMapping("/courses")
@CrossOrigin
public class CourseController {

    private final CourseService courseService;
    private final ModuleService moduleService;

    public CourseController(CourseService courseService,
                            ModuleService moduleService) {
        this.courseService = courseService;
        this.moduleService = moduleService;
    }

    //CREATE COURSE
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    // GET ALL COURSES 
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // COURSE DETAILS
    @GetMapping("/{courseId}/details")
    public CourseDetailsDTO getCourseDetails(@PathVariable Long courseId) {
    //path variable  extracts courseId from the URL
        Course course = courseService.getCourseById(courseId);
        List<Module> modules = moduleService.getModulesByCourse(courseId);
    /*etch the course AND its modules separately, 
    then wrap both into one CourseDetailsDTO object, 
    so Angular gets everything in one API call instead of two. */
        return new CourseDetailsDTO(course, modules);
    }



    //GET COURSE BY ID
    @GetMapping("/{courseId}")
    public Course getCourseById(@PathVariable Long courseId) {
        return courseService.getCourseById(courseId);
    }

   
    //  TRAINER COURSES

    @GetMapping("/trainer/{trainerId}")
    public List<Course> getTrainerCourses(@PathVariable Long trainerId) {
        return courseService.getTrainerCourses(trainerId);
    }

  
    //  SUBMIT FOR REVIEW
   
    @PutMapping("/{courseId}/submit")
    public Course submitCourse(@PathVariable Long courseId) {
        return courseService.submitForReview(courseId);
    }


    //  ADMIN: PENDING
   
    @GetMapping("/pending")
    public List<Course> getPendingCourses() {
        return courseService.getPendingCourses();
    }

   
    // ADMIN: APPROVE
  
    @PutMapping("/{courseId}/approve/{adminId}")
    public Course approveCourse(@PathVariable Long courseId,
                                @PathVariable Long adminId) {
        return courseService.approveCourse(courseId, adminId);
    }

    
    // ADMIN: REJECT
 
    @PutMapping("/{courseId}/reject")
    public Course rejectCourse(@PathVariable Long courseId) {
        return courseService.rejectCourse(courseId);
    }
    

// STUDENT: PUBLISHED COURSES

@GetMapping("/published")
public List<Course> getPublishedCourses() {
    return courseService.getPublishedCourses();
}



}



/* 
CourseController
   ↓
fetch course
   ↓
fetch modules
   ↓
wrap both into DTO
   ↓
send to frontend

*/