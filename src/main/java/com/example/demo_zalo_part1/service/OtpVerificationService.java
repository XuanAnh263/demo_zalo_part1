package com.example.demo_zalo_part1.service;

import com.example.demo_zalo_part1.entity.Otp;
import com.example.demo_zalo_part1.entity.OtpVerification;
import com.example.demo_zalo_part1.entity.User;
import com.example.demo_zalo_part1.repository.OtpRepository;
import com.example.demo_zalo_part1.repository.OtpVerificationRepository;
import com.example.demo_zalo_part1.repository.UserRepository;
import com.example.demo_zalo_part1.statics.OtpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpVerificationService {
    OtpVerificationRepository otpVerificationRepository;
    OtpRepository otpRepository;
    UserRepository userRepository;
    public boolean verifyOtp(String email, String code, UUID sessionId) {
        Otp otp = otpRepository.findBySessionId(sessionId);
        Optional<User> user = userRepository.findByEmail(email);


        if (otp != null && otp.getCode().equals(code) && otp.getExpiry().isAfter(LocalDateTime.now())) {
                OtpVerification otpVerification = new OtpVerification();

                otpVerification.setUserId(user.get());
                otpVerification.setOtp(otp);
                otpVerification.setSession(sessionId);
                otpVerification.setStatus(OtpStatus.VERIFIED);
                otpVerification.setSuccess(true);
                otpVerification.setVerificationTime(LocalDateTime.now());
                otpVerification.setCreateDateTime(LocalDateTime.now());
                otpVerification.setLastModifiedDateTime(LocalDateTime.now());

                otpVerificationRepository.save(otpVerification);
                return true;
            }

        return false;
    }
}
