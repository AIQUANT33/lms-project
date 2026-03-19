package com.example.demo.service;

import com.example.demo.entity.UiTheme;
import com.example.demo.repository.UiThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UiService {

    @Autowired
    private UiThemeRepository themeRepo;

   public UiTheme getActiveTheme() {
    return themeRepo.findByIsActive(true);
}
}