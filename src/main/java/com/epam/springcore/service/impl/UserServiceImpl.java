package com.epam.springcore.service.impl;

import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.InvalidCredentialsException;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.UserMapper;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.UserRepository;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.request.user.UpdatePasswordRequest;
import com.epam.springcore.service.IUserService;
import com.epam.springcore.util.CredentialGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final CredentialGenerator credentialGenerator;
    private final UserMapper userMapper;

    @Override
    public boolean authenticate(String username, String password) {
        log.info("Authenticating user: {}", username);
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            log.warn("Authentication failed: User not found: {}", username);
            return false;
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(password)) {
            log.warn("Authentication failed: Incorrect password for username: {}", username);
            return false;
        }

        if (!user.isUserActive()) {
            log.warn("Authentication failed: User is not active: {}", username);
            return false;
        }

        log.info("Authentication successful for user: {}", username);
        return true;
    }


    @Override
    public UserDto getUserByUsername(String targetUsername) {
        log.info("Fetching user with username: {}", targetUsername);
        User user = checkIfUserExistByUsername(targetUsername);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    @Override
    public void deleteUser(String targetUsername) {
        log.info("Deleting user with username: {}", targetUsername);
        User user = checkIfUserExistByUsername(targetUsername);
        userRepository.delete(user);
        log.info("User deleted successfully: {}", targetUsername);
    }

    @Override
    public UserDto updatePassword(String username,UpdatePasswordRequest request) {
        log.info("Updating password for user: {}", username);
        User user = checkIfUserExistByUsername(username);

        if (!user.getPassword().equals(request.getOldPassword())) {
            log.warn("Old password does not match for user: {}", username);
            throw new InvalidCredentialsException("Old password does not match.");
        }

        user.setPassword(request.getNewPassword());
        User updatedUser = userRepository.save(user);
        log.info("Password updated successfully for user: {}", updatedUser.getUsername());
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void activateOrDeactivate( String targetUsername) {
        log.info("Toggling activation status for user: {} by admin: {}", targetUsername);
        User user = getUserEntityByUsername(targetUsername);
        user.setUserActive(!user.isUserActive());
        userRepository.save(user);
        log.info("User '{}' activation status changed to: {}", targetUsername, user.isUserActive());
    }


    @Override
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }

    @Override
    public User createUserEntity(CreateUserRequest request) {
        log.info("Creating new user with name: {} {}", request.getFirstName(), request.getLastName());
        User user = userMapper.createUser(request);
        user.setUsername(credentialGenerator.generateUsername(request.getFirstName(), request.getLastName(), userRepository.findAll()));
        user.setPassword(credentialGenerator.generateRandomPassword());
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    private User checkIfUserExistByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User with username " + username + " not found"));
    }
}