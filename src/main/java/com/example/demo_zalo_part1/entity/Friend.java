package com.example.demo_zalo_part1.entity;

import com.example.demo_zalo_part1.statics.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Data
@Builder
@ToString
@Entity
@Table(name = "friends")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = User.class)
            @JoinColumn(name = "user_id")
    User userId;

    @ManyToOne(targetEntity = User.class)
            @JoinColumn(name = "friend_id")
    User friendId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    Status status;

    public Friend(User userId, User friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
}
