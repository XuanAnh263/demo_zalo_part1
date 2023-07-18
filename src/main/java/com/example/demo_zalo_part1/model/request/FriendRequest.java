package com.example.demo_zalo_part1.model.request;

import com.example.demo_zalo_part1.entity.User;
import com.example.demo_zalo_part1.statics.FriendStatus;
import com.example.demo_zalo_part1.statics.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendRequest {


    Long userId;
    Long friendId;
    FriendStatus status;

}
