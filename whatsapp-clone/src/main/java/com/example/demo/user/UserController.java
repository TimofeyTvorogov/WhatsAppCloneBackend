package com.example.demo.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User")
public class UserController {
    private final UserService userService;
    @GetMapping
    ResponseEntity<List<UserResponse>> getAllUsersExceptSelf(Authentication connectedUser) {
        return ResponseEntity.ok(userService.findAllUsersExceptSelf(connectedUser));
    }
}
