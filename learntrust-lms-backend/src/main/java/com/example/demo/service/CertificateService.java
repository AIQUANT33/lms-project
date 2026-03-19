package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.demo.dto.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.List;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final BlockchainService blockchainService;
    private final PinataService pinataService;

    @Value("${platform.wallet.address}")
    private String platformWalletAddress;

    public CertificateService(
            CertificateRepository certificateRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            BlockchainService blockchainService,
            PinataService pinataService) {

        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.blockchainService = blockchainService;
        this.pinataService = pinataService;
    }

    public Certificate issueCertificate(Long studentId, Long courseId) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = enrollmentRepository
                .findByStudentAndCourse(student, course)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!"COMPLETED".equals(enrollment.getCompletionStatus()) && 
            !"APPROVED".equals(enrollment.getCompletionStatus())) {
            throw new RuntimeException("Course not completed yet");
        }

        // Prevent duplicate certificates
        if (!certificateRepository
                .findByStudentAndCourse(student, course)
                .isEmpty()) {

            throw new IllegalStateException("Certificate already issued");
        }

        String rawData =
                student.getUserId() +
                student.getEmail() +
                course.getCourseId() +
                LocalDateTime.now();

        String certificateHash = generateSHA256(rawData);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("name", "LearnTrust Certificate");
        metadata.put("studentName", student.getName());
        metadata.put("courseName", course.getTitle());
        metadata.put("certificateHash", certificateHash);
        metadata.put("issuedAt", LocalDateTime.now().toString());

        String tokenURI = pinataService.uploadMetadata(metadata);

        if (tokenURI == null || tokenURI.isBlank()) {
            throw new RuntimeException("Pinata upload failed: tokenURI is null");
        }

        BlockchainService.MintResponse mintResponse;

        try {

            mintResponse = blockchainService.mintCertificate(
                    platformWalletAddress,
                    certificateHash,
                    tokenURI
            );

        } catch (Exception e) {
            throw new RuntimeException("Blockchain mint failed: " + e.getMessage());
        }

        Certificate certificate = new Certificate();
        certificate.setStudent(student);
        certificate.setCourse(course);
        certificate.setCertificateHash(certificateHash);
        certificate.setBlockchainTransactionHash(
                mintResponse.getTransactionHash()
        );
        certificate.setMetadataUrl(tokenURI);
        certificate.setIssuedAt(LocalDateTime.now());

        if (mintResponse.getTokenId() != null) {
            certificate.setTokenId(
                    mintResponse.getTokenId().longValue()
            );                  
        }

        return certificateRepository.save(certificate);
    }
public CertificateVerificationDTO verifyCertificate(String hash) {

    Certificate certificate = certificateRepository
            .findByCertificateHash(hash)
            .orElseThrow(() ->
                    new RuntimeException("Certificate not found"));

    CertificateVerificationDTO dto = new CertificateVerificationDTO();

    dto.setStudentName(certificate.getStudent().getName());
    dto.setStudentEmail(certificate.getStudent().getEmail());

    dto.setCourseTitle(certificate.getCourse().getTitle());

    dto.setCertificateHash(certificate.getCertificateHash());
    dto.setBlockchainTransactionHash(certificate.getBlockchainTransactionHash());
    dto.setIssuedAt(certificate.getIssuedAt().toString());
    dto.setMetadataUrl(certificate.getMetadataUrl());
    dto.setTokenId(certificate.getTokenId());

    return dto;
}
   

  public Certificate getCertificate(Long studentId, Long courseId) {

    User student = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));

    Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));

    List<Certificate> certificates =
            certificateRepository.findByStudentAndCourse(student, course);

    // If certificate already exists → return latest
    if (!certificates.isEmpty()) {
        return certificates.get(certificates.size() - 1);
    }

    // If certificate does NOT exist → create one automatically
    return issueCertificate(studentId, courseId);
}

    private String generateSHA256(String input) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            input.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {

            throw new RuntimeException("Hash generation failed");
        }
    }
}




/*
Checks the student completed the course, 
generates the SHA-256 hash,
 calls PinataService to upload metadata to IPFS, 
 calls BlockchainService to mint the NFT,
  then saves everything to PostgreSQL. 


*/