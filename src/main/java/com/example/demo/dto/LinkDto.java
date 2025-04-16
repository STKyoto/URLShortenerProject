package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class LinkDto {
        private int id;
        private String originalUrl;
        private String shortUrl;
        private int userId;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private int clickCount;
}
