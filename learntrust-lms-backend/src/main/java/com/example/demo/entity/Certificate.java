package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "certificate_hash", nullable = false, unique = true)
    private String certificateHash;

    @Column(name = "blockchain_transaction_hash", nullable = false)
    private String blockchainTransactionHash;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;
   
   
    @Column
     private String metadataUrl;

     @Column
     private Long tokenId;


    public Certificate() {
        this.issuedAt = LocalDateTime.now();
    }
}
