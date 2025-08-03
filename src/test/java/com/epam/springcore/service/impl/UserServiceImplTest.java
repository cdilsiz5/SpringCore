package com.epam.springcore.service.impl;

import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.InvalidCredentialsException;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.UserMapper;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.UserRepository;
import com.epam.springcore.request.user.ChangePasswordRequest;
import com.epam.springcore.request.user.LoginRequest;
import com.epam.springcore.util.CredentialGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CredentialGenerator credentialGenerator;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MDC.put("transactionId", "TEST-TX-001");

        testUser = User.builder()
                .id(1L)
                .username("john.doe")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .userActive(false)
                .build();

        testUserDto = UserDto.builder()
                .id(1L)
                .username("john.doe")
                .firstName("John")
                .lastName("Doe")
                .userActive(false)
                .build();

        loginRequest = LoginRequest.builder()
                .username("john.doe")
                .password("password123")
                .build();
    }


    @Test
    void login_ShouldReturnTrue_WhenCredentialsAreValid() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        boolean result = userService.login(loginRequest);

        assertTrue(result);
        assertTrue(testUser.isUserActive());
        verify(userRepository).save(testUser);
    }

    @Test
    void login_ShouldThrowInvalidCredentialsException_WhenUserNotFound() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(loginRequest)
        );

        assertEquals("Invalid username or password", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldThrowInvalidCredentialsException_WhenPasswordIsWrong() {
        testUser.setPassword("differentPassword");
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(loginRequest)
        );

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void logout_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.logout("john.doe")
        );

        assertEquals("User not found: john.doe", exception.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    void isAuthenticated_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.isAuthenticated("john.doe")
        );

        assertEquals("User not found: john.doe", exception.getMessage());
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenOldPasswordIsCorrect() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .username("john.doe")
                .oldPassword("password123")
                .newPassword("newPassword456")
                .build();

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword(request);

        assertEquals("newPassword456", testUser.getPassword());
        verify(userRepository).save(testUser);
    }

    @Test
    void changePassword_ShouldThrowInvalidCredentialsException_WhenOldPasswordIsWrong() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .username("john.doe")
                .oldPassword("wrongPassword")
                .newPassword("newPassword456")
                .build();

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> userService.changePassword(request)
        );

        assertEquals("Old password does not match", exception.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    void getUserByUsername_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
        when(userMapper.toUserDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.getUserByUsername("john.doe");

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void getUserByUsername_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserByUsername("john.doe")
        );

        assertEquals("User not found: john.doe", exception.getMessage());
    }


    @Test
    void getAllUsers_ShouldReturnUserDtoList_WhenUsersExist() {
        List<User> users = List.of(testUser);
        List<UserDto> userDtos = List.of(testUserDto);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserDtoList(users)).thenReturn(userDtos);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john.doe", result.get(0).getUsername());
    }


    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));

        userService.deleteUser("john.doe");

        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.deleteUser("john.doe")
        );

        assertEquals("User not found: john.doe", exception.getMessage());
        verify(userRepository, never()).delete(any());
    }


    @Test
    void activateOrDeactivate_ShouldToggleUserStatus_WhenUserExists() {
        testUser.setUserActive(false);
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.activateOrDeactivate("john.doe");

        assertTrue(testUser.isUserActive());
        verify(userRepository).save(testUser);
    }

    @Test
    void activateOrDeactivate_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.activateOrDeactivate("john.doe")
        );

        assertEquals("User not found: john.doe", exception.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    void getUserEntityByUsername_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(testUser));

        User result = userService.getUserEntityByUsername("john.doe");

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void getUserEntityByUsername_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserEntityByUsername("john.doe")
        );

        assertEquals("User not found: john.doe", exception.getMessage());
    }




}