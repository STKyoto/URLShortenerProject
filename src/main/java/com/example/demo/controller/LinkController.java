package com.example.demo.controller;

import com.example.demo.dto.LinkDto;
import com.example.demo.dto.LinkRequestDto;
import com.example.demo.mapper.LinkMapper;
import com.example.demo.model.Link;
import com.example.demo.service.LinkService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/UrlShortener/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/create")
    public LinkDto createLink(@RequestParam String originalUrl,
                              @AuthenticationPrincipal String username,
                              @RequestParam(required = false) String expiresAt) {
        LocalDateTime expiration = null;
        if (expiresAt != null && !expiresAt.isEmpty()) {
            expiration = LocalDateTime.parse(expiresAt);
        }

        Link createdLink = linkService.createShortLink(originalUrl, username, expiration);
        return LinkMapper.toDto(createdLink);
    }

    @GetMapping("/{shortUrl}")
    public LinkDto getLink(@PathVariable String shortUrl) {
        Optional<Link> linkOpt = linkService.getLinkByShortUrl(shortUrl);
        Link link = linkOpt.orElseThrow(() -> new RuntimeException("Link not found"));

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This link has expired");
        }

        return LinkMapper.toDto(link);
    }

    @PostMapping("/{shortUrl}/click")
    public void recordClick(@PathVariable String shortUrl) {
        linkService.recordClick(shortUrl);
    }

    @GetMapping("/{shortUrl}/stats")
    public long getClickStats(@PathVariable String shortUrl) {
        return linkService.getClickCountByShortUrl(shortUrl);
    }
}

