package com.example.demo.dto;

import lombok.Data;

@Data
public class CertificateVerificationDTO {

    private String studentName;
    private String studentEmail;

    private String courseTitle;

    private String certificateHash;
    private String blockchainTransactionHash;
    private String issuedAt;
    private String metadataUrl;
    private Long tokenId;
}