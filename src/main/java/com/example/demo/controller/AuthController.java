package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.UserRegRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/UrlShortener/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.authenticateUser(request);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        User user = new User(request.getUsername(), request.getPassword());
        userService.registerUser(user);

        AuthRequest authRequest = new AuthRequest(request.getUsername(), request.getPassword());
        AuthResponse tokenResponse = authService.authenticateUser(authRequest);

        return ResponseEntity.ok(tokenResponse);
    }
}
