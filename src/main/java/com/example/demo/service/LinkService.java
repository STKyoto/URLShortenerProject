package com.example.demo.service;

import com.example.demo.model.Link;
import com.example.demo.model.User;
import com.example.demo.repository.LinkRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
