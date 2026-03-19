package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "module_contents")
public class ModuleContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contentType; // VIDEO, PDF, TEXT

    private String contentUrl;

    @Column(length = 5000)
    private String contentText;

    @Column(nullable = false)
    private int sequenceOrder;
}

// a piece of content (VIDEO/PDF/TEXT) inside a module, has a sequence order