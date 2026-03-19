package com.example.demo.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class VideoUploadService {

    private final Cloudinary cloudinary;

    public VideoUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadVideo(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of(
                            "resource_type", "video",
                            "folder", "learntrust/videos"
                    )
            );

            return result.get("secure_url").toString();

        } catch (Exception e) {
            throw new RuntimeException("Video upload failed", e);
        }
    }
}