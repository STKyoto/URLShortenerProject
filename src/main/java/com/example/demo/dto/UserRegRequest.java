package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegRequest {

    @NotBlank(message = "Username is required")
    private String username;
    private String password;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long and include uppercase, lowercase letters, and digits"
    )

    public UserRegRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}