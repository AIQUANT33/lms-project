package com.example.demo.controller;

import com.example.demo.service.VideoUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("/videos")
public class VideoUploadController {

    private final VideoUploadService videoUploadService;

    public VideoUploadController(VideoUploadService videoUploadService) {
        this.videoUploadService = videoUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(
            @RequestParam("file") MultipartFile file) {

        String url = videoUploadService.uploadVideo(file);
        return ResponseEntity.ok(url);
    }
}