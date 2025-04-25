package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LinkTest {

    @Test
    void testNoArgsConstructor() {
        Link link = new Link();
        assertNotNull(link);
        assertNotNull(link.getCreatedAt(), "createdAt should be initialized");
        assertEquals(0, link.getClickCount(), "clickCount should default to 0");
    }

    @Test
    void testAllArgsConstructorAndGettersSetters() {
        User user = new User("john", "password123");
        LocalDateTime expiresAt = LocalDateTime.of(2030, 1, 1, 0, 0);

        Link link = new Link("https://example.com", "short123", user, expiresAt);

        assertEquals("https://example.com", link.getOriginalUrl());
        assertEquals("short123", link.getShortUrl());
        assertEquals(user, link.getUser());
        assertEquals(expiresAt, link.getExpiresAt());

        link.setId(42);
        link.setClickCount(5);
        assertEquals(42, link.getId());
        assertEquals(5, link.getClickCount());
    }

    @Test
    void testClickCountIncrementSimulation() {
        Link link = new Link();
        assertEquals(0, link.getClickCount());

        link.setClickCount(link.getClickCount() + 1);
        assertEquals(1, link.getClickCount());

        link.setClickCount(link.getClickCount() + 2);
        assertEquals(3, link.getClickCount());
    }

    @Test
    void testSetCreatedAtManually() {
        Link link = new Link();
        LocalDateTime customCreated = LocalDateTime.of(2020, 5, 10, 10, 30);
        link.setCreatedAt(customCreated);
        assertEquals(customCreated, link.getCreatedAt());
    }
}

