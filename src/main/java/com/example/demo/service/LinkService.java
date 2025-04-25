package com.example.demo.service;

import com.example.demo.dto.LinkDto;
import com.example.demo.mapper.LinkMapper;
import org.springframework.security.access.AccessDeniedException;
import com.example.demo.model.Link;
import com.example.demo.model.User;
import com.example.demo.repository.LinkRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class LinkService {

    private static final Random random = new Random();
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    public LinkService(LinkRepository linkRepository, UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Link createShortLink(String originalUrl, String username, LocalDateTime expiresAt) {
        validateShortLink(originalUrl);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        String shortUrl = generateShortUrl();
        while (linkRepository.existsByShortUrl(shortUrl)) {
            shortUrl = generateShortUrl();
        }
        Link link = new Link(originalUrl, shortUrl, user, expiresAt);
        return linkRepository.save(link);
    }

    private String generateShortUrl() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 6 + random.nextInt(3);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public Optional<Link> getLinkByShortUrl(String shortUrl) {
        return linkRepository.findByShortUrl(shortUrl);
    }

    @Transactional
    public void recordClick(String shortUrl) {
        Optional<Link> linkOpt = linkRepository.findByShortUrl(shortUrl);

        if (linkOpt.isPresent()) {
            linkRepository.incrementClickCount(linkOpt.get().getId());
        } else {
            throw new RuntimeException("Link not found");
        }
    }

    public void validateShortLink(String originalUrl) {
        if (originalUrl.length() > 2048) {
            throw new IllegalArgumentException("Original URL is too long");
        }
        try {
            new URL(originalUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + originalUrl, e);
        }
    }

    public long getClickCountByShortUrl(String shortUrl) {
        Optional<Link> linkOpt = linkRepository.findByShortUrl(shortUrl);
        if (linkOpt.isPresent()) {
            return linkOpt.get().getClickCount();
        } else {
            throw new RuntimeException("Link not found");
        }
    }

    public List<LinkDto> getAllUserLinks(String username) {
        User user = findUserByUsername(username);
        List<Link> links = linkRepository.findByUser(user);
        return links.stream()
                .map(LinkMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LinkDto> getActiveUserLinks(String username) {
        User user = findUserByUsername(username);
        List<Link> activeLinks = linkRepository.findActiveLinksByUser(user, LocalDateTime.now());
        return activeLinks.stream()
                .map(LinkMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserLink(String shortUrl, String username) {
        User user = findUserByUsername(username);
        Link link = linkRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("Link not found with shortUrl: " + shortUrl));


        if (link.getUser().getId() != user.getId()) {
            throw new AccessDeniedException("User " + username + " is not authorized to delete link " + shortUrl);
        }

        linkRepository.delete(link);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

}