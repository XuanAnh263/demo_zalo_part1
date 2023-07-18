package com.example.demo_zalo_part1.model.response;

import com.example.demo_zalo_part1.statics.OtpStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpResponse {
    String email;
    String code;
    OtpStatus status;
    UUID sessionId;
    LocalDateTime expiry;
}
