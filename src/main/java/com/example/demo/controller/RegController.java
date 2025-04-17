package com.example.demo.controller;

import com.example.demo.dto.UserRegRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/UrlShortener/auth")
@RequiredArgsConstructor
public class RegController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        User user = new User(request.getUsername(), request.getPassword());
        userService.registerUser(user);

        return ResponseEntity.ok("User registered successfully");
    }
}
