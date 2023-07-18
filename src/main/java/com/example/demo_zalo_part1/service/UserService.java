package com.example.demo_zalo_part1.service;

import com.example.demo_zalo_part1.entity.Otp;
import com.example.demo_zalo_part1.entity.User;
import com.example.demo_zalo_part1.exception.*;
import com.example.demo_zalo_part1.model.request.*;
import com.example.demo_zalo_part1.model.response.JwtResponse;
import com.example.demo_zalo_part1.model.response.UserResponse;
import com.example.demo_zalo_part1.repository.RefreshTokenRepository;
import com.example.demo_zalo_part1.repository.UserRepository;
import com.example.demo_zalo_part1.security.CustomUserDetails;
import com.example.demo_zalo_part1.security.JwtUtils;
import com.example.demo_zalo_part1.security.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {

    final PasswordEncoder passwordEncoder;

    final UserRepository userRepository;

//    final RoleRepository roleRepository;

    final ObjectMapper objectMapper;

    final RefreshTokenRepository refreshTokenRepository;

    OtpService otpService;
    OtpVerificationService otpVerificationService;

    @Value("${application.security.refreshToken.tokenValidityMilliseconds}")
    long refreshTokenValidityMilliseconds;

    final JwtUtils jwtUtils;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                       ObjectMapper objectMapper,
                       RefreshTokenRepository refreshTokenRepository, JwtUtils jwtUtils) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtils = jwtUtils;
    }

    public void signUpRequest(SignUpRequest request) {
//        Optional<Role> optionalRole = roleRepository.findByName(Roles.USER);
//        Set<Role> roles = new HashSet<>();
//        roles.add(optionalRole.get());
//        String password = request.getPassword();
//        String confirmPassword = request.getConfirmPassword();
//
//        if (!Objects.equals(password, confirmPassword)) {
//            throw new InvalidPasswordException("Password mismatch");
//        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .username(request.getUsername())
//                .roles(roles)
                .build();
        userRepository.save(user);
    }


    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();
        if (!CollectionUtils.isEmpty(users)) {
            return users.stream().map(u -> objectMapper.convertValue(u, UserResponse.class)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public UserResponse getDetail(Long id) throws ClassNotFoundException {
        return userRepository.findById(id).map(u -> objectMapper.convertValue(u, UserResponse.class)).orElseThrow(ClassNotFoundException::new);
    }

    public JwtResponse refreshToken(RefreshTokenRequest request) throws RefreshTokenNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String newToken = userRepository.findById(userDetails.getId())
                .flatMap(user -> refreshTokenRepository.findByUserAndRefreshTokenAndInvalidated(user, request.getRefreshToken(), false)
                        .map(refreshToken -> {
                            LocalDateTime createdDateTime = refreshToken.getCreatedDateTime();
                            LocalDateTime expiryTime = createdDateTime.plusSeconds(refreshTokenValidityMilliseconds / 1000);
                            if (expiryTime.isBefore(LocalDateTime.now())) {
                                refreshToken.setInvalidated(true);
                                refreshTokenRepository.save(refreshToken);
                                return null;
                            }
                            return jwtUtils.generateJwtToken(authentication);
                        }))
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));


        if (newToken == null) {
            throw new RefreshTokenNotFoundException();
        }
        return JwtResponse.builder()
                .jwt(newToken)
                .build();
    }

    @Transactional
    public void logout() {
        Optional<Long> userIdOptional = SecurityUtils.getCurrentUserLoginId();
        if (userIdOptional.isEmpty()) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại");
        }
        refreshTokenRepository.logOut(userIdOptional.get());
        SecurityContextHolder.clearContext();
    }


    public Optional<User> resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String otpCode = request.getOtpCode();;
        UUID sessionId = request.getSessionId();
        String newPassword = request.getNewPassword();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new InvalidOtpException("Invalid OTP code");
        }

//        boolean isVerified = otpVerificationService.verifyOtp(email, otpCode, sessionId);
//        if (!isVerified) {
//            throw new EmailNotFoundException("Email dose not exist.");
//        }
        userOptional.ifPresent(user -> {
            user.setPassword(newPassword);
            userRepository.save(user);
        });
        return Optional.empty();
    }

    public void sendOtpEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User with email " + email + " not found");
        }

        Otp otp = otpService.generateOtp();
        if (LocalDateTime.now().isAfter(otp.getExpiry())) {
            throw new InvalidOtpException("Otp expired");
        }
        otpService.sendOtpEmail(email, otp.getCode());
    }

    public void changePassword(User user, ChangePasswordRequest request) {
//        validate password
        //TODO
        if (!BCrypt.checkpw(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect password~");
        }
        String hash = BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt(12));
        user.setPassword(hash);
        userRepository.save(user);
    }
}
