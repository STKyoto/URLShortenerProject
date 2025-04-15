package com.example.demo.controller;


import com.example.demo.model.Link;
import com.example.demo.service.LinkService;
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
    public Link createLink(@RequestParam String originalUrl, @RequestParam String username, @RequestParam(required = false) String expiresAt) {
        LocalDateTime expiration = null;

        if (expiresAt != null && !expiresAt.isEmpty()) {
            expiration = LocalDateTime.parse(expiresAt);
        }
        return linkService.createShortLink(originalUrl, username, expiration);
    }


    @GetMapping("/{shortUrl}")
    public Link getLink(@PathVariable String shortUrl) {
        Optional<Link> linkOpt = linkService.getLinkByShortUrl(shortUrl);


        Link link = linkOpt.orElseThrow(() -> new RuntimeException("Link not found"));

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This link has expired");
        }

        return linkOpt.orElseThrow(() -> new RuntimeException("Link not found"));
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
