package com.example.demo.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class VideoUploadService {
     //Spring sees the constructor needs a Cloudinary object → looks for a @Bean of type Cloudinary → finds the one in CloudinaryConfig → injects it here.
    private final Cloudinary cloudinary;

    public VideoUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadVideo(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(), //converts the file into a raw byte array.
                    Map.of(
                            "resource_type", "video", //tells Cloudinary this is a video
                            "folder", "learntrust/videos"
                    )
            );
            
            /*Cloudinary returns a Map with lots of info about the uploaded file. we only need secure_url which is the permanent HTTPS URL.
             This is what gets returned to the controller and saved in your database. */
            return result.get("secure_url").toString();

        } catch (Exception e) {
            throw new RuntimeException("Video upload failed", e);
        }
    }
}


//entire cloudinary flow
/*
Trainer picks file
      ↓
Browser sends file DIRECTLY to Cloudinary (no Spring Boot involved)
      ↓
Cloudinary stores the file, returns a URL
      ↓
Angular saves that URL in a variable
      ↓
Trainer clicks Save → Angular sends URL to Spring Boot
      ↓
Spring Boot saves the URL to PostgreSQL
      ↓
Student opens course → Angular reads URL from DB
      ↓
Browser loads file directly from Cloudinary


*/