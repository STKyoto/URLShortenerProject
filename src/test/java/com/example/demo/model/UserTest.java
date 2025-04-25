package com.example.demo.model;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNotNull(user.getLinks(), "Links list should be initialized");
        assertTrue(user.getLinks().isEmpty(), "Links list should be empty");
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User("alice", "securepass");

        assertEquals("alice", user.getUsername());
        assertEquals("securepass", user.getPassword());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        user.setId(1);
        user.setUsername("bob");
        user.setPassword("hash123");


        assertEquals(1, user.getId());
        assertEquals("bob", user.getUsername());
        assertEquals("hash123", user.getPassword());

    }

    @Test
    void testAddAndRemoveLink() {
        User user = new User("john", "password");
        Link link1 = new Link();
        link1.setOriginalUrl("https://example.com");
        link1.setShortUrl("abc123");
        link1.setUser(user);
        user.getLinks().add(link1);
        assertEquals(1, user.getLinks().size());
        assertEquals(link1, user.getLinks().get(0));
        user.getLinks().remove(link1);
        assertTrue(user.getLinks().isEmpty());
    }

    @Test
    void testLinksListReference() {
        User user = new User();
        List<Link> links = user.getLinks();
        links.add(new Link());
        assertEquals(1, user.getLinks().size(), "Modifications on the returned list should affect the original list");
    }
}

