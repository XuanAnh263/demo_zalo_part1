package com.example.demo_zalo_part1.model.request;

import com.example.demo_zalo_part1.statics.OtpStatus;
import com.example.demo_zalo_part1.statics.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;


@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpVerificationRequest extends BaseRequest {


    UserRequest user;
    OtpRequest otp;
    UUID session;
    OtpStatus status;

    @NotNull(message = "success is required")
    Boolean success;


    LocalDateTime verificationTime;

}
