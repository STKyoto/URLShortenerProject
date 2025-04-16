package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkRequestDto {
    private String originalUrl;
    private String expiresAt;
}
