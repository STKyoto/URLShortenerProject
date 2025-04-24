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
    public LinkDto createLink(@RequestBody LinkRequestDto request,
                              @AuthenticationPrincipal String username) {
        LocalDateTime expiration = null;
        if (request.getExpiresAt() != null && !request.getExpiresAt().isEmpty()) {
            expiration = LocalDateTime.parse(request.getExpiresAt());
        }
        Link createdLink = linkService.createShortLink(request.getOriginalUrl(), username, expiration);
        return LinkMapper.toDto(createdLink);
    }

    @GetMapping("/{shortUrl}")
    public LinkDto getLink(@PathVariable String shortUrl) {
        Optional<Link> linkOpt = linkService.getLinkByShortUrl(shortUrl);
        Link link = linkOpt.orElseThrow(() -> new RuntimeException("Link not found"));
        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This link has expired");
        }
        linkService.recordClick(shortUrl);
        return LinkMapper.toDto(link);
    }

    @GetMapping("/{shortUrl}/stats")
    public long getClickStats(@PathVariable String shortUrl) {
        return linkService.getClickCountByShortUrl(shortUrl);
    }
}

