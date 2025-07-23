package com.epam.springcore.service.impl;

import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.InvalidCredentialsException;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.UserMapper;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.UserRepository;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.request.user.UpdatePasswordRequest;
import com.epam.springcore.util.CredentialGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Tests for UserServiceImpl")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CredentialGenerator credentialGenerator;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("authenticate - returns true for valid credentials and active user")
    void shouldAuthenticateSuccessfully_WhenCredentialsValid() {
        User user = User.builder()
                .username("Cihan.Dilsiz")
                .password("1234")
                .userActive(true)
                .build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));

        boolean result = userService.authenticate("Cihan.Dilsiz", "1234");
        assertTrue(result);
    }

    @Test
    @DisplayName("authenticate - returns false when password is incorrect")
    void shouldFailAuthentication_WhenPasswordIncorrect() {
        User user = User.builder()
                .username("Cihan.Dilsiz")
                .password("correct-pass")
                .userActive(true)
                .build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));

        boolean result = userService.authenticate("Cihan.Dilsiz", "wrong-pass");
        assertFalse(result);
    }

    @Test
    @DisplayName("authenticate - returns false when user is inactive")
    void shouldFailAuthentication_WhenUserIsInactive() {
        User user = User.builder()
                .username("Cihan.Dilsiz")
                .password("1234")
                .userActive(false)
                .build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));

        boolean result = userService.authenticate("Cihan.Dilsiz", "1234");
        assertFalse(result);
    }

    @Test
    @DisplayName("authenticate - returns false when user not found")
    void shouldFailAuthentication_WhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        boolean result = userService.authenticate("unknown", "any");
        assertFalse(result);
    }

    @Test
    @DisplayName("getUserByUsername - should return user when exists")
    void shouldReturnUser_WhenExists() {
        User user = User.builder().username("Cihan.Dilsiz").build();
        UserDto dto = UserDto.builder().username("Cihan.Dilsiz").build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(dto);

        UserDto result = userService.getUserByUsername("Cihan.Dilsiz");
        assertNotNull(result);
        assertEquals("Cihan.Dilsiz", result.getUsername());
    }

    @Test
    @DisplayName("getUserByUsername - should throw if user not found")
    void shouldThrow_WhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserByUsername("ghost"));
    }

    @Test
    @DisplayName("updatePassword - should update if old password correct")
    void shouldUpdatePassword_WhenOldPasswordMatches() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("old", "new");
        User user = User.builder().username("Cihan.Dilsiz").password("old").userActive(true).build();
        UserDto dto = UserDto.builder().username("Cihan.Dilsiz").build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toUserDto(any(User.class))).thenReturn(dto);

        UserDto result = userService.updatePassword("Cihan.Dilsiz", request);

        assertNotNull(result);
        assertEquals("Cihan.Dilsiz", result.getUsername());
        assertEquals("new", user.getPassword());
    }

    @Test
    @DisplayName("updatePassword - should throw when old password is wrong")
    void shouldThrow_WhenOldPasswordWrong() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("wrong", "new");
        User user = User.builder().username("Cihan.Dilsiz").password("old").build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> userService.updatePassword("Cihan.Dilsiz", request));
    }

    @Test
    @DisplayName("updatePassword - should throw if user not found")
    void shouldThrow_WhenUserNotFoundForPasswordUpdate() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("old", "new");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updatePassword("ghost", request));
    }

    @Test
    @DisplayName("deleteUser - should delete when user exists")
    void shouldDeleteUser_WhenExists() {
        User user = User.builder().username("Cihan.Dilsiz").build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser("Cihan.Dilsiz");

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteUser - should throw when user not found")
    void shouldThrow_WhenDeletingUnknownUser() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser("ghost"));
    }

    @Test
    @DisplayName("activateOrDeactivate - should toggle active flag")
    void shouldToggleActivation() {
        User user = User.builder().username("Cihan.Dilsiz").userActive(true).build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));

        userService.activateOrDeactivate("Cihan.Dilsiz");

        assertFalse(user.isUserActive());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("activateOrDeactivate - should throw when user not found")
    void shouldThrow_WhenTogglingUnknownUser() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.activateOrDeactivate("ghost"));
    }
}
