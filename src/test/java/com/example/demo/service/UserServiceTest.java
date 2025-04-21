package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userMapper = mock(UserMapper.class);
        userService = new UserService(userRepository, passwordEncoder, userMapper);
    }

    @Test
    void testGetUserByUsername_found() {
        User user = new User("testuser", "pass");
        user.setEmail("test@example.com");
        UserDto expectedDto = new UserDto("testuser", "test@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        Optional<UserDto> result = userService.getUserByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testRegisterUser_success() {
        User user = new User("newuser", "rawpass");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");

        userService.registerUser(user);

        assertThat(user.getPassword()).isEqualTo("encodedpass");
        verify(userRepository).save(user);
    }

    @Test
    void testRegisterUser_usernameExists() {
        User user = new User("existing", "pass");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void testLoadUserByUsername_found() {
        User user = new User("john", "hashedpass");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("john");

        assertThat(userDetails.getUsername()).isEqualTo("john");
        assertThat(userDetails.getPassword()).isEqualTo("hashedpass");
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }

    @Test
    void testLoadUserByUsername_notFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User("user1", "pass1");
        user1.setEmail("u1@email.com");
        User user2 = new User("user2", "pass2");
        user2.setEmail("u2@email.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(userMapper.toDto(user1)).thenReturn(new UserDto("user1", "u1@email.com"));
        when(userMapper.toDto(user2)).thenReturn(new UserDto("user2", "u2@email.com"));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(1).getEmail()).isEqualTo("u2@email.com");
    }

    @Test
    void testFindByUsername_success() {
        User user = new User("targetuser", "pass");
        user.setEmail("target@email.com");
        UserDto dto = new UserDto("targetuser", "target@email.com");

        when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.findByUsername("targetuser");

        assertThat(result.getUsername()).isEqualTo("targetuser");
        assertThat(result.getEmail()).isEqualTo("target@email.com");
    }

    @Test
    void testFindByUsername_notFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("ghost"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void testUpdateUser_success() {
        long userId = 1L;
        User user = new User("oldname", "pass");
        user.setEmail("old@email.com");

        UserDto inputDto = new UserDto("newname", "new@email.com");
        UserDto outputDto = new UserDto("newname", "new@email.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(outputDto);

        UserDto updated = userService.updateUser(userId, inputDto);

        assertThat(user.getUsername()).isEqualTo("newname");
        assertThat(user.getEmail()).isEqualTo("new@email.com");
        assertThat(updated.getUsername()).isEqualTo("newname");
        assertThat(updated.getEmail()).isEqualTo("new@email.com");

        verify(userRepository).save(user);
    }
}


