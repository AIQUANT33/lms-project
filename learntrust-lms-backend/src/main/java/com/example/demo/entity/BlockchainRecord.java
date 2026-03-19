package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "blockchain_records")
public class BlockchainRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blockchainId;

    @OneToOne //one blockchain record belongs to exactly one certificate, and one certificate has exactly one blockchain record. 
    @JoinColumn(name = "certificate_id", nullable = false, unique = true)
    private Certificate certificate;
  
     @Column(nullable = false)
    private String nftTokenId;

    @Column(nullable = false)
    private String transactionHash;

      @Column(nullable = false)
    private LocalDateTime mintedAt;

    @Column(nullable = false)
    private String network;
    // Ethereum Testnet

    public BlockchainRecord() {
        this.mintedAt = LocalDateTime.now();
    }

    public BlockchainRecord(Certificate certificate, String transactionHash, String network) {
        this.certificate = certificate;
        this.transactionHash = transactionHash;
        this.network = network;
    }

    public Long getBlockchainId() {
        return blockchainId;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public String getTransactionHash() {
        return transactionHash;
    }
}



/*
A separate audit table that links a certificate to its blockchain details
 — transaction hash, NFT token ID, which network (Ethereum testnet), and when it was minted.
  Exists as an extra layer of record keeping separate from the main certificate.

*/