package com.example.demo_zalo_part1.repository;

import com.example.demo_zalo_part1.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByCode (String otp);
     Otp findBySessionId(UUID sessionId);
}
