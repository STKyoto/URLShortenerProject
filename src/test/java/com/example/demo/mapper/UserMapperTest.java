package com.example.demo.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void shouldMapUserToDto() {

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@example.com", dto.getEmail());
    }

    @Test
    void shouldMapDtoToUser() {

        UserDto dto = new UserDto("john", "john@example.com");

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("john", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void shouldReturnNullWhenUserIsNull() {
        assertNull(userMapper.toDto(null));
    }

    @Test
    void shouldReturnNullWhenDtoIsNull() {
        assertNull(userMapper.toEntity(null));
    }
}

