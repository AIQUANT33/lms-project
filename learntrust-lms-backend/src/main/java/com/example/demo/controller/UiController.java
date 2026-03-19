package com.example.demo.controller;

import com.example.demo.entity.UiTheme;
import com.example.demo.service.UiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ui")
@CrossOrigin
public class UiController {

    @Autowired
    private UiService uiService;

    @GetMapping("/theme")
    public UiTheme getTheme() {
        return uiService.getActiveTheme();
    }
}