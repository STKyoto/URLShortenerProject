package com.example.demo.service;

import com.example.demo.dto.LinkMapper;
import com.example.demo.dto.LinkRequestDto;
import com.example.demo.dto.LinkResponseDto;
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

    private final LinkRepository linkRepository;
    private final UserRepository userRepository;



    public LinkService(LinkRepository linkRepository, UserRepository userRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
    }

    public LinkResponseDto createShortLink(LinkRequestDto requestDto, String username) {
        String originalUrl = requestDto.getOriginalUrl();
        validateOriginalUrl(originalUrl);

        LocalDateTime expiration = null;
        if (requestDto.getExpiresAt() != null && !requestDto.getExpiresAt().isEmpty()) {
            expiration = LocalDateTime.parse(requestDto.getExpiresAt());
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String shortUrl = generateShortUrl();
        while (linkRepository.existsByShortUrl(shortUrl)) {
            shortUrl = generateShortUrl();
        }

        Link link = new Link(originalUrl, shortUrl, user, expiration);
        Link savedLink = linkRepository.save(link);

        return LinkMapper.toDto(savedLink);
    }

        private String generateShortUrl () {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            Random random = new Random();
            int length = 6 + random.nextInt(3);
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(characters.charAt(random.nextInt(characters.length())));
            }
            return sb.toString();
        }

        public LinkResponseDto  getLinkByShortUrl (String shortUrl){
            Link link = linkRepository.findByShortUrl(shortUrl).orElseThrow(() -> new RuntimeException("Link not found"));
            return LinkMapper.toDto(link);
        }

        @Transactional
        public void recordClick (String shortUrl){
            Optional<Link> linkOpt = linkRepository.findByShortUrl(shortUrl);

            if (linkOpt.isPresent()) {
                linkRepository.incrementClickCount(linkOpt.get().getId());
            } else {
                throw new RuntimeException("Link not found");
            }
        }


        public void validateOriginalUrl (String originalUrl){
            if (originalUrl.length() > 2048) {
                throw new IllegalArgumentException("Original URL is too long");
            }
            try {
                new URL(originalUrl);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid URL: " + originalUrl, e);
            }
        }

        public long getClickCountByShortUrl (String shortUrl){
            Optional<Link> linkOpt = linkRepository.findByShortUrl(shortUrl);
            if (linkOpt.isPresent()) {
                return linkOpt.get().getClickCount();
            } else {
                throw new RuntimeException("Link not found");
            }
        }


}
