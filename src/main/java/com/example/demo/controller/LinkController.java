package com.example.demo.controller;


import com.example.demo.dto.LinkRequestDto;
import com.example.demo.dto.LinkResponseDto;
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
        LinkRequestDto requestDto = new LinkRequestDto();
        requestDto.setOriginalUrl(originalUrl);
        requestDto.setExpiresAt(expiresAt);
        return linkService.createShortLink(requestDto, username);
    }


    @GetMapping("/{shortUrl}")
    public Link getLink(@PathVariable String shortUrl) {
        LinkResponseDto responseDto = linkService.getLinkByShortUrl(shortUrl);
        if (responseDto.getExpiresAt() != null && responseDto.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This link has expired");
        }
        return responseDto;
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
