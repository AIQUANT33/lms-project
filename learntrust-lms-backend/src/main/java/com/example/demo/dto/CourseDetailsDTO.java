package com.example.demo.dto;

import com.example.demo.entity.Course;
import com.example.demo.entity.Module;
import java.util.List;

//used only for api response

public class CourseDetailsDTO { 

    private Course course; //this willl hold course basic info (title, description, status, etc.)
    private List<Module> modules; //holds all modules of that cuorse

    public CourseDetailsDTO(Course course, List<Module> modules) {
        this.course = course;
        this.modules = modules;
    }

    public Course getCourse() { return course; } //getters used to convert object to json (without getters json may be empty)
    public List<Module> getModules() { return modules; }
}

/* 
Java stores:

course → into DTO.course

modules → into DTO.modules

This prepares the response object.

*/



/* 
✅ combined data
✅ clean API response
✅ avoid multiple API calls
✅ avoid lazy loading issues
✅ control response shape



*/