package com.example.demo_zalo_part1.repository;

import com.example.demo_zalo_part1.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
}
