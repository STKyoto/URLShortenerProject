package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")

public class RegController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(@RequestParam String username, @RequestParam String password) {

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters long and include uppercase, lowercase letters, and digits");
        }

        User user = userService.create(username, password);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
}