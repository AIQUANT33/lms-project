package com.example.demo.service;

import com.example.demo.dto.ModuleContentRequest;
import com.example.demo.entity.Module;
import com.example.demo.entity.ModuleContent;
import com.example.demo.repository.ModuleContentRepository;
import com.example.demo.repository.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleContentService {

    private final ModuleContentRepository contentRepository;
    private final ModuleRepository moduleRepository;

    public ModuleContentService(ModuleContentRepository contentRepository,
                                ModuleRepository moduleRepository) {
        this.contentRepository = contentRepository;
        this.moduleRepository = moduleRepository;
    }

    // CREATE CONTENT
    public ModuleContent createContent(ModuleContentRequest request) {

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));

        ModuleContent content = new ModuleContent();
        content.setModule(module);
        content.setTitle(request.getTitle());
        content.setContentType(request.getContentType());
        content.setContentUrl(request.getContentUrl());
        content.setContentText(request.getContentText());
        content.setSequenceOrder(request.getSequenceOrder());

        return contentRepository.save(content);
    }

    // GET CONTENTS
    public List<ModuleContent> getContentsByModule(Long moduleId) {

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        return contentRepository.findByModuleOrderBySequenceOrderAsc(module);
    }

    // UPDATE CONTENT
    public ModuleContent updateContent(Long contentId, ModuleContent updated) {

        ModuleContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        content.setTitle(updated.getTitle());
        content.setContentType(updated.getContentType());
        content.setContentUrl(updated.getContentUrl());
        content.setContentText(updated.getContentText());

        return contentRepository.save(content);
    }

    // DELETE CONTENT
    public void deleteContent(Long contentId) {
        contentRepository.deleteById(contentId);
    }
}