package com.example.demo.dto;

import lombok.Data;

@Data
public class ModuleContentRequest {

    private Long moduleId;
    private String title;
    private String contentType;
    private String contentUrl;
    private String contentText;
    private int sequenceOrder;
}
