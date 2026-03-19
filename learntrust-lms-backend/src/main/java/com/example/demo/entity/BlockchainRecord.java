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

    @OneToOne
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

