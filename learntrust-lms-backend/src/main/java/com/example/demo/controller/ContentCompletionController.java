package com.example.demo.controller;

import com.example.demo.dto.ContentCompletionRequest;
import com.example.demo.entity.ContentCompletion;
import com.example.demo.service.ContentCompletionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content-completions")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:44313"})
public class ContentCompletionController {

    private final ContentCompletionService completionService;

    public ContentCompletionController(ContentCompletionService completionService) {
        this.completionService = completionService;
    }

    @PostMapping
    public ContentCompletion completeContent(@RequestBody ContentCompletionRequest request) {
        return completionService.completeContent(request);
    }
}
