package com.example.demo.repository;

import com.example.demo.entity.Certificate;
import com.example.demo.entity.User;
import com.example.demo.entity.Course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByCertificateHash(String certificateHash);

    // Return list to avoid NonUniqueResultException
    List<Certificate> findByStudentAndCourse(User student, Course course);

}