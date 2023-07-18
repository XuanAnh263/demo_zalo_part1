package com.example.demo_zalo_part1.repository;

import com.example.demo_zalo_part1.entity.Friend;
import com.example.demo_zalo_part1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
}
