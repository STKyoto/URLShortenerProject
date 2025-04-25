package com.example.demo.controller;

import com.example.demo.dto.LinkDto;
import com.example.demo.dto.LinkRequestDto;
import com.example.demo.mapper.LinkMapper;
import com.example.demo.model.Link;
import com.example.demo.service.LinkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/UrlShortener/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/create")
    public ResponseEntity<LinkDto> createLink(@RequestBody LinkRequestDto request,
                                              @AuthenticationPrincipal String username) {

        LocalDateTime expiration = null;
        if (request.getExpiresAt() != null && !request.getExpiresAt().isEmpty()) {
            try {
                expiration = LocalDateTime.parse(request.getExpiresAt());
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        try {
            Link createdLink = linkService.createShortLink(request.getOriginalUrl(), username, expiration);
            return ResponseEntity.status(HttpStatus.CREATED).body(LinkMapper.toDto(createdLink));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    public ResponseEntity<Long> getClickStats(@PathVariable String shortUrl) {
        try {

            if (linkService.getLinkByShortUrl(shortUrl).isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            long count = linkService.getClickCountByShortUrl(shortUrl);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<LinkDto>> getAllMyLinks(@AuthenticationPrincipal String username) {
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<LinkDto> userLinks = linkService.getAllUserLinks(username);
        return ResponseEntity.ok(userLinks);
    }

    @GetMapping("/my/active")
    public ResponseEntity<List<LinkDto>> getMyActiveLinks(@AuthenticationPrincipal String username) {
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<LinkDto> activeUserLinks = linkService.getActiveUserLinks(username);
        return ResponseEntity.ok(activeUserLinks);
    }

    @DeleteMapping("/{shortUrl}")
    public ResponseEntity<Void> deleteMyLink(@PathVariable String shortUrl,
                                             @AuthenticationPrincipal String username) {
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            linkService.deleteUserLink(shortUrl, username);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }
}