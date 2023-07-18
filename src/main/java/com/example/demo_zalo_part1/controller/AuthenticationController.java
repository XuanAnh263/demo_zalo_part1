package com.example.demo_zalo_part1.controller;

import com.example.demo_zalo_part1.entity.RefreshToken;
import com.example.demo_zalo_part1.exception.InvalidOtpException;
import com.example.demo_zalo_part1.exception.RefreshTokenNotFoundException;
import com.example.demo_zalo_part1.model.request.LoginRequest;
import com.example.demo_zalo_part1.model.request.RefreshTokenRequest;
import com.example.demo_zalo_part1.model.request.ResetPasswordRequest;
import com.example.demo_zalo_part1.model.request.SignUpRequest;
import com.example.demo_zalo_part1.model.response.JwtResponse;
import com.example.demo_zalo_part1.repository.RefreshTokenRepository;
import com.example.demo_zalo_part1.repository.UserRepository;
import com.example.demo_zalo_part1.security.CustomUserDetails;
import com.example.demo_zalo_part1.security.JwtUtils;
import com.example.demo_zalo_part1.service.OtpService;
import com.example.demo_zalo_part1.service.OtpVerificationService;
import com.example.demo_zalo_part1.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/authentication")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    JwtUtils jwtUtils;

    UserService userService;

    OtpService otpService;
    UserRepository userRepository;


    RefreshTokenRepository refreshTokenRepository;

    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public JwtResponse authenticateUser(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        Set<String> roles = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toSet());

//        Set<String> roles = Collections.singleton("USER");

        String refreshToken = UUID.randomUUID().toString();
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(userRepository.findById(userDetails.getId()).get())
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return JwtResponse.builder()
                .jwt(jwt)
                .refreshToken(refreshToken)
                .id(userDetails.getId())
                .username(userDetails.getUsername())
//                .role("USER")
                .build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(user -> new ResponseEntity<>("Username is existed", HttpStatus.BAD_REQUEST))
                .orElseGet(() -> {
                    userService.signUpRequest(request);
                    return new ResponseEntity<>(null, HttpStatus.CREATED);
                });
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        try {
            return ResponseEntity.ok(userService.refreshToken(request));
        } catch (RefreshTokenNotFoundException | UsernameNotFoundException ex) {
            return new ResponseEntity<>("Thông tin refreshToken không chính xác", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        userService.logout();
        return ResponseEntity.ok(null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        return Optional.ofNullable(userService.resetPassword(request))
                .map(user -> {
                        otpService.sendOtpEmail(request.getEmail(), request.getOtpCode());
                        return  ResponseEntity.ok("Reset password successful. OTp has been sent to your email");
                }).orElse(ResponseEntity.badRequest().body("user not found"));
    }

}
