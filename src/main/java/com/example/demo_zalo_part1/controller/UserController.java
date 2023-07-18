package com.example.demo_zalo_part1.controller;
import com.example.demo_zalo_part1.model.response.UserResponse;
import com.example.demo_zalo_part1.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;


    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponse getDetail(@PathVariable Long id) throws ClassNotFoundException {
        return userService.getDetail(id);
    }





}
