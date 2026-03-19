package com.example.demo.controller;

import com.example.demo.dto.ModuleContentRequest;
import com.example.demo.entity.ModuleContent;
import com.example.demo.service.ModuleContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/module-contents")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:44313"})
public class ModuleContentController {

    private final ModuleContentService contentService;

    public ModuleContentController(ModuleContentService contentService) {
        this.contentService = contentService;
    }

    // CREATE CONTENT
    @PostMapping
    public ModuleContent createContent(@RequestBody ModuleContentRequest request) {
        return contentService.createContent(request);
    }

    // GET CONTENTS BY MODULE
    @GetMapping("/module/{moduleId}")
    public List<ModuleContent> getContents(@PathVariable Long moduleId) {
        return contentService.getContentsByModule(moduleId);
    }

    // UPDATE CONTENT
    @PutMapping("/{contentId}")
    public ModuleContent updateContent(
            @PathVariable Long contentId,
            @RequestBody ModuleContent updated) {

        return contentService.updateContent(contentId, updated);
    }

    // DELETE CONTENT
    @DeleteMapping("/{contentId}")
    public void deleteContent(@PathVariable Long contentId) {
        contentService.deleteContent(contentId);
    }
}