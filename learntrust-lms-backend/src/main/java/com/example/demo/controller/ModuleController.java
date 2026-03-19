package com.example.demo.controller;

import com.example.demo.entity.Module;
import com.example.demo.service.ModuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modules")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:44313"})
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    // ================= CREATE =================
    @PostMapping
    public Module createModule(@RequestParam Long courseId,
                               @RequestBody Module module) {
        return moduleService.createModule(courseId, module);
    }

    // ================= GET BY COURSE =================
    @GetMapping("/course/{courseId}")
    public List<Module> getModulesByCourse(@PathVariable Long courseId) {
        return moduleService.getModulesByCourse(courseId);
    }
    @PutMapping("/{moduleId}/move-up")
public void moveUp(@PathVariable Long moduleId) {
    moduleService.moveModuleUp(moduleId);
}

@PutMapping("/{moduleId}/move-down")
public void moveDown(@PathVariable Long moduleId) {
    moduleService.moveModuleDown(moduleId);
}

    // ================= DELETE =================
    @DeleteMapping("/{moduleId}")
    public void deleteModule(@PathVariable Long moduleId) {
        moduleService.deleteModule(moduleId);
    }
}