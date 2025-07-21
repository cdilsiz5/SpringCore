package com.epam.springcore.service.impl;

import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.exception.InvalidCredentialsException;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.UserRepository;
import com.epam.springcore.request.user.LoginRequest;
import com.epam.springcore.request.user.UpdatePasswordRequest;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.util.CredentialGenerator;
import com.epam.springcore.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Tests for UserServiceImpl")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ITrainerService trainerService;

    @Mock
    private ITraineeService traineeService;

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
    @DisplayName("getUserByUsername - should return user when user exists")
    void shouldReturnUser_WhenUsernameExists() {
        User user = User.builder().username("Cihan.Dilsiz").firstName("Cihan").lastName("Dilsiz").isActive(true).build();
        UserDto userDto = UserDto.builder().username("Cihan.Dilsiz").firstName("Cihan").lastName("Dilsiz").isActive(true).build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserByUsername("Cihan.Dilsiz");

        assertNotNull(result);
        assertEquals("Cihan.Dilsiz", result.getUsername());
    }

    @Test
    @DisplayName("getUserByUsername - should throw exception when user not found")
    void shouldThrowException_WhenUsernameNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserByUsername("unknown"));
    }


    @Test
    @DisplayName("login - should return true for correct credentials")
    void shouldLoginSuccessfully_WhenCredentialsCorrect() {
        LoginRequest request = new LoginRequest("Cihan.Dilsiz", "1234");
        User user = User.builder().username("Cihan.Dilsiz").password("1234").isActive(true).build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));

        boolean result = userService.login(request);
        assertTrue(result);
    }

    @Test
    @DisplayName("login - should throw exception when password is incorrect")
    void shouldThrowException_WhenPasswordIncorrect() {
        LoginRequest request = new LoginRequest("Cihan.Dilsiz", "wrong");
        User user = User.builder().username("Cihan.Dilsiz").password("1234").isActive(true).build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }


    @Test
    @DisplayName("login - should throw NotFoundException when user not found")
    void shouldThrow_WhenUserNotFoundDuringLogin() {
        LoginRequest request = new LoginRequest("nonexist", "1234");

        when(userRepository.findByUsername("nonexist")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.login(request));
    }


    @Test
    @DisplayName("updatePassword - should update password when old password matches")
    void shouldUpdatePassword_WhenUserExists() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("oldPass", "newPass");
        User user = User.builder().username("Cihan.Dilsiz").password("oldPass").isActive(true).build();
        UserDto userDto = UserDto.builder().username("Cihan.Dilsiz").build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.updatePassword("Cihan.Dilsiz", request);

        assertNotNull(result);
        assertEquals("Cihan.Dilsiz", result.getUsername());
    }

    @Test
    @DisplayName("updatePassword - should throw exception when user not found")
    void shouldThrow_WhenUpdatingPasswordOfNonexistentUser() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("old", "new");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updatePassword("ghost", request));
    }

    @Test
    @DisplayName("updatePassword - should throw exception when old password is wrong")
    void shouldThrow_WhenOldPasswordDoesNotMatch() {
        UpdatePasswordRequest request = new UpdatePasswordRequest("wrong", "new");
        User user = User.builder().username("Cihan.Dilsiz").password("oldPass").build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class, () -> userService.updatePassword("Cihan.Dilsiz", request));
    }


    @Test
    @DisplayName("deleteUser - should delete user if exists")
    void shouldDeleteUser_WhenExists() {
        User user = User.builder().username("Cihan.Dilsiz").build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser("Cihan.Dilsiz");

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteUser - should throw exception when user not found")
    void shouldThrow_WhenDeletingNonexistentUser() {
        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser("Cihan.Dilsiz"));
    }


    @Test
    @DisplayName("activateOrDeactivate - should toggle activation status")
    void shouldToggleUserActivationStatus() {
        User user = User.builder().username("Cihan.Dilsiz").isActive(true).build();

        when(userRepository.findByUsername("Cihan.Dilsiz")).thenReturn(Optional.of(user));

        userService.activateOrDeactivate("Cihan.Dilsiz");

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("activateOrDeactivate - should throw exception when user not found")
    void shouldThrow_WhenTogglingActivationOfUnknownUser() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.activateOrDeactivate("ghost"));
    }
}
