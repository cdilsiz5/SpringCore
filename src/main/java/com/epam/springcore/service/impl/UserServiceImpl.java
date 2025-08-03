package com.epam.springcore.service.impl;

import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.InvalidCredentialsException;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.UserMapper;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.UserRepository;
import com.epam.springcore.request.user.ChangePasswordRequest;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.request.user.LoginRequest;
import com.epam.springcore.service.IUserService;
import com.epam.springcore.util.CredentialGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CredentialGenerator credentialGenerator;


    @Override
    @Transactional
    public boolean login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed - username not found: {}", request.getUsername());
                    return new InvalidCredentialsException("Invalid username or password");
                });

        if (!user.getPassword().equals(request.getPassword())) {
            log.warn("Login failed - wrong password for user: {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid  password");
        }

        user.setUserActive(true);
        userRepository.save(user);
        log.info("Login successful - user '{}' is now active", user.getUsername());
        return true;
    }

    @Override
    @Transactional
    public void logout(String username) {
        log.info("Logging out user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Logout failed - user not found: {}", username);
                    return new NotFoundException("User not found: " + username);
                });

        user.setUserActive(false);
        userRepository.save(user);
        log.info("Logout successful - user '{}' is now inactive", username);
    }

    @Override
    public boolean isAuthenticated(String username) {
        log.debug("Checking isActive for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        boolean isAuth = user.isUserActive();
        log.debug("User '{}' authenticated: {}", username, isAuth);
        return isAuth;
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        log.info("Changing password for user: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!user.getPassword().equals(request.getOldPassword())) {
            log.warn("Password change failed - incorrect old password for user: {}", request.getUsername());
            throw new InvalidCredentialsException("Old password does not match");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", request.getUsername());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto getUserByUsername(String targetUsername) {
        log.debug("Fetching user DTO by username: {}", targetUsername);
        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new NotFoundException("User not found: " + targetUsername));

        return userMapper.toUserDto(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Retrieving all users");
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteUser(String targetUsername) {
        log.info("Deleting user: {}", targetUsername);

        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new NotFoundException("User not found: " + targetUsername));

        userRepository.delete(user);
        log.info("User '{}' deleted successfully", targetUsername);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void activateOrDeactivate(String targetUsername) {
        log.info("Toggling activation for user: {}", targetUsername);

        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new NotFoundException("User not found: " + targetUsername));

        user.setUserActive(!user.isUserActive());
        userRepository.save(user);

        log.info("User '{}' is now {}", targetUsername, user.isUserActive() ? "ACTIVE" : "INACTIVE");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserEntityByUsername(String username) {
        log.debug("Fetching user entity by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User createUserEntity(CreateUserRequest request) {
        log.info("Creating new user for {} {}", request.getFirstName(), request.getLastName());

        String username = credentialGenerator.generateUsername(request.getFirstName(), request.getLastName(),userRepository.findAll());
        String password = credentialGenerator.generateRandomPassword();

        User user = User.builder()
                .username(username)
                .password(password)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userActive(false)
                .build();

        User savedUser = userRepository.save(user);

        log.info("User created with username: {}", savedUser.getUsername());
        return savedUser;
    }
}
