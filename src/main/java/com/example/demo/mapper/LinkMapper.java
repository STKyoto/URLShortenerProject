package com.example.demo.mapper;

import com.example.demo.dto.LinkDto;
import com.example.demo.model.Link;
import com.example.demo.model.User;

public class LinkMapper {

    public static LinkDto toDto(Link link) {
        LinkDto dto = new LinkDto();
        dto.setId(link.getId());
        dto.setOriginalUrl(link.getOriginalUrl());
        dto.setShortUrl(link.getShortUrl());
        dto.setUserId(link.getUser().getId());
        dto.setCreatedAt(link.getCreatedAt());
        dto.setExpiresAt(link.getExpiresAt());
        dto.setClickCount(link.getClickCount());
        return dto;
    }

    public static Link toEntity(LinkDto dto, User user) {
        Link link = new Link();
        link.setOriginalUrl(dto.getOriginalUrl());
        link.setShortUrl(dto.getShortUrl());
        link.setUser(user);
        link.setExpiresAt(dto.getExpiresAt());
        link.setClickCount(dto.getClickCount());
        return link;
    }
}
