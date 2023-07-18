package com.example.demo_zalo_part1.model.response;

import com.example.demo_zalo_part1.statics.FriendStatus;
import com.example.demo_zalo_part1.statics.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data

@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendResponse {

    Long userId;
//    UserResponse friend;
    FriendStatus status;
    String message;

    public FriendResponse(Long userId, FriendStatus status) {
        this.userId = userId;
        this.status = status;
    }
}
