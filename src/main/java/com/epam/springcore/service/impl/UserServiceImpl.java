package com.epam.springcore.service.impl;

import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.InvalidCredentialsException;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.UserRepository;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.request.user.LoginRequest;
import com.epam.springcore.request.user.UpdatePasswordRequest;
import com.epam.springcore.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.epam.springcore.mapper.UserMapper.USER_MAPPER;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(CreateUserRequest request) {
        log.info("Creating user with Name : {}", request.getFirstName()+" "+request.getLastName());
        User user = USER_MAPPER.createUser(request);
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUsername());
        return USER_MAPPER.toUserDto(savedUser);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        log.info("Fetching user with username: {}", username);
        User user = checkIfUserExistByUsername(username);
        return USER_MAPPER.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        return USER_MAPPER.toUserDtoList(userRepository.findAll());
    }

    @Override
    public void deleteUser(String username) {
        log.info("Deleting user with username: {}", username);
        User user = checkIfUserExistByUsername(username);
        userRepository.delete(user);
        log.info("User deleted successfully: {}", username);
    }

    @Override
    public UserDto updatePassword(String username, UpdatePasswordRequest request) {
        log.info("Updating password for user: {}", username);
        User user = checkIfUserExistByUsername(username);

        if (!user.getPassword().equals(request.getOldPassword())) {
            log.warn("Old password does not match for user: {}", username);
            throw new InvalidCredentialsException("Old password does not match.");
        }

        user.setPassword(request.getNewPassword());
        User updatedUser = userRepository.save(user);
        log.info("Password updated successfully for user: {}", username);
        return USER_MAPPER.toUserDto(updatedUser);
    }

    @Override
    public boolean login(LoginRequest request) {
        log.info("Attempting login for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found with username {}", request.getUsername());
                    return new InvalidCredentialsException("Invalid username or password");
                });

        if (!user.getPassword().equals(request.getPassword())) {
            log.warn("Login failed: Incorrect password for username {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (!user.isActive()) {
            log.warn("Login failed: User is not active: {}", request.getUsername());
            throw new InvalidCredentialsException("User is not active");
        }

        log.info("Login successful for username: {}", request.getUsername());
        return true;
    }

    private User checkIfUserExistByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with username %s not found", username)
                ));
    }
}
