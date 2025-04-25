package com.example.demo.service;

import com.example.demo.model.Link;
import com.example.demo.model.User;
import com.example.demo.repository.LinkRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LinkServiceTest {

    private LinkRepository linkRepository;
    private UserRepository userRepository;
    private LinkService linkService;

    @BeforeEach
    void setUp() {
        linkRepository = mock(LinkRepository.class);
        userRepository = mock(UserRepository.class);
        linkService = new LinkService(linkRepository, userRepository);
    }

    @Test
    void testCreateShortLink_success() {
        String originalUrl = "https://example.com";
        String username = "user1";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(linkRepository.existsByShortUrl(anyString())).thenReturn(false);
        when(linkRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Link link = linkService.createShortLink(originalUrl, username, expiresAt);

        assertThat(link).isNotNull();
        assertThat(link.getOriginalUrl()).isEqualTo(originalUrl);
        assertThat(link.getShortUrl()).isNotBlank();
        assertThat(link.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(link.getUser()).isEqualTo(user);
    }

    @Test
    void testCreateShortLink_userNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> linkService.createShortLink("https://example.com", "unknown", null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void testCreateShortLink_invalidUrl() {
        String badUrl = "invalid-url";

        assertThatThrownBy(() -> linkService.createShortLink(badUrl, "user", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid URL");
    }

    @Test
    void testValidateShortLink_tooLong() {
        String longUrl = "http://example.com/" + "a".repeat(2049);

        assertThatThrownBy(() -> linkService.validateShortLink(longUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Original URL is too long");
    }

    @Test
    void testGetLinkByShortUrl_found() {
        Link link = new Link();
        when(linkRepository.findByShortUrl("abc123")).thenReturn(Optional.of(link));

        Optional<Link> result = linkService.getLinkByShortUrl("abc123");

        assertThat(result).isPresent().contains(link);
    }

    @Test
    void testRecordClick_success() {
        Link link = new Link();
        link.setId(1);
        when(linkRepository.findByShortUrl("xyz")).thenReturn(Optional.of(link));

        linkService.recordClick("xyz");

        verify(linkRepository).incrementClickCount(1);
    }

    @Test
    void testRecordClick_notFound() {
        when(linkRepository.findByShortUrl("notfound")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> linkService.recordClick("notfound"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Link not found");
    }

    @Test
    void testGetClickCountByShortUrl_found() {
        Link link = new Link();
        link.setClickCount(5);
        when(linkRepository.findByShortUrl("short")).thenReturn(Optional.of(link));

        long count = linkService.getClickCountByShortUrl("short");

        assertThat(count).isEqualTo(5);
    }

    @Test
    void testGetClickCountByShortUrl_notFound() {
        when(linkRepository.findByShortUrl("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> linkService.getClickCountByShortUrl("missing"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Link not found");
    }
}
