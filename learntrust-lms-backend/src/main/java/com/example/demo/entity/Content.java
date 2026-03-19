package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contentUrl;

    @Column(nullable = false)
    private int contentOrder;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    public Content() {}
}