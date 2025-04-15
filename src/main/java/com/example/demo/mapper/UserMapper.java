package com.example.demo.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.model.User;

public class UserMapper {
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getUsername(), user.getEmail());
    }

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        return user;
    }
}
