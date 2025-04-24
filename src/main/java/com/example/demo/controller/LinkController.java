package com.example.demo.controller;

import com.example.demo.dto.LinkDto;
import com.example.demo.dto.LinkRequestDto;
import com.example.demo.mapper.LinkMapper;
import com.example.demo.model.Link;
import com.example.demo.service.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/UrlShortener/links")
public class LinkController {

    private final LinkService linkService;
    private final String baseUrl = "http://localhost:8080/UrlShortener/links";

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLink(@RequestBody LinkRequestDto request,
                                        @AuthenticationPrincipal String username) {

        if (request.getOriginalUrl() == null || request.getOriginalUrl().isEmpty()) {
            return ResponseEntity.badRequest().body("Original URL must be provided.");
        }

        if (request.getExpiresAt() == null || request.getExpiresAt().isEmpty()) {
            return ResponseEntity.badRequest().body("Expiration date must be provided.");
        }

        LocalDateTime expiration;
        try {
            expiration = LocalDateTime.parse(request.getExpiresAt());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Expiration date format is invalid.");
        }

        Link createdLink = linkService.createShortLink(request.getOriginalUrl(), username, expiration);
        String shortUrlWithBase = baseUrl + "/" + createdLink.getShortUrl();
        LinkDto linkDto = LinkMapper.toDto(createdLink);
        linkDto.setShortUrl(shortUrlWithBase);

        return ResponseEntity.ok(linkDto);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> redirectToOriginal(@PathVariable String shortUrl) {
        Optional<Link> linkOpt = linkService.getLinkByShortUrl(shortUrl);
        if (!linkOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Link not found");
        }

        Link link = linkOpt.get();

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body("This link has expired");
        }

        linkService.recordClick(shortUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(link.getOriginalUrl()))
                .build();
    }


    @GetMapping("/{shortUrl}/stats")
    public long getClickStats(@PathVariable String shortUrl) {
        return linkService.getClickCountByShortUrl(shortUrl);
    }
}

