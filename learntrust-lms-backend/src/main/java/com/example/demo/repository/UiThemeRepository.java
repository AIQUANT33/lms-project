package com.example.demo.repository;

import com.example.demo.entity.UiTheme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UiThemeRepository extends JpaRepository<UiTheme, Long> {

    UiTheme findByIsActive(boolean isActive);
}