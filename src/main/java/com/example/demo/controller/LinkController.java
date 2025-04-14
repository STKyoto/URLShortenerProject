package com.example.demo.controller;


import com.example.demo.model.Link;
import com.example.demo.repository.LinkRepository;
import com.example.demo.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/shorturl/links")
public class LinkController {
    private final LinkService linkService;

    @Autowired
    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/create")
    public Link createLink(@RequestParam String originalUrl, @RequestParam String username, @RequestParam(required = false) String expiresAt) {
        LocalDateTime expiration = null;

        //convert from String to LocalDateTime format
        if (expiresAt != null && !expiresAt.isEmpty()) {
            expiration = LocalDateTime.parse(expiresAt);
        }
        return linkService.createShortLink(originalUrl, username, expiration);
    }

    // endpoint to get link by short URL
    @GetMapping("/{shortUrl}")
    public Link getLink(@PathVariable String shortUrl) {
        Optional<Link> linkOpt = linkService.getLinkByShortUrl(shortUrl);


        //check for a link and expiration date
        Link link = linkOpt.orElseThrow(() -> new RuntimeException("Link not found"));

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This link has expired");
        }

        return linkOpt.orElseThrow(() -> new RuntimeException("Link not found"));
    }


    //to reg a click
    @PostMapping("/{shortUrl}/click")
    public void recordClick(@PathVariable String shortUrl) {
        linkService.recordClick(shortUrl);
    }
}
