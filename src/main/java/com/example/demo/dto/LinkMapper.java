package com.example.demo.dto;

import com.example.demo.model.Link;

public class LinkMapper {

    public static LinkResponseDto toDto(Link link) {
        LinkResponseDto dto = new LinkResponseDto();
        dto.setId(link.getId());
        dto.setOriginalUrl(link.getOriginalUrl());
        dto.setShortUrl(link.getShortUrl());
        dto.setCreatedAt(link.getCreatedAt());
        dto.setExpiresAt(link.getExpiresAt());
        dto.setClickCount(link.getClickCount());
        return dto;
    }

    public static Link toEntity(LinkRequestDto dto) {
        Link link = new Link();
        link.setOriginalUrl(dto.getOriginalUrl());
        return link;
    }
}
