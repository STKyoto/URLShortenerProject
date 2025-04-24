package com.example.demo.mapper;

import com.example.demo.dto.LinkDto;
import com.example.demo.model.Link;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LinkMapperTest {

    @Test
    void shouldMapEntityToDtoCorrectly() {

        User user = new User();
        user.setId(42);
        user.setUsername("testuser");

        Link link = new Link();
        link.setId(1);
        link.setOriginalUrl("https://example.com");
        link.setShortUrl("abc123");
        link.setUser(user);
        link.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));
        link.setExpiresAt(LocalDateTime.of(2024, 12, 31, 23, 59));
        link.setClickCount(5);

        LinkDto dto = LinkMapper.toDto(link);

        assertEquals(link.getId(), dto.getId());
        assertEquals(link.getOriginalUrl(), dto.getOriginalUrl());
        assertEquals(link.getShortUrl(), dto.getShortUrl());
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(link.getCreatedAt(), dto.getCreatedAt());
        assertEquals(link.getExpiresAt(), dto.getExpiresAt());
        assertEquals(link.getClickCount(), dto.getClickCount());
    }

    @Test
    void shouldMapDtoToEntityCorrectly() {

        LinkDto dto = new LinkDto();
        dto.setOriginalUrl("https://example.org");
        dto.setShortUrl("xyz987");
        dto.setExpiresAt(LocalDateTime.of(2025, 1, 1, 0, 0));
        dto.setClickCount(10);

        User user = new User();
        user.setId(99);
        user.setUsername("john");

        Link link = LinkMapper.toEntity(dto, user);

        assertEquals(dto.getOriginalUrl(), link.getOriginalUrl());
        assertEquals(dto.getShortUrl(), link.getShortUrl());
        assertEquals(dto.getExpiresAt(), link.getExpiresAt());
        assertEquals(dto.getClickCount(), link.getClickCount());
        assertEquals(user, link.getUser());
    }
}
