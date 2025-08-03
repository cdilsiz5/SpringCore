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
import com.epam.springcore.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private UserRepository userRepository;
    private UserMapper userMapper;
    @Autowired
    private CredentialGenerator credentialGenerator;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    @Override
    public boolean login(LoginRequest request) {
        String txId = LogUtil.getTransactionId();
        log.info("[{}] SERVICE Layer - Attempting login for user: {}", txId, request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - Login failed - username not found: {}", txId, request.getUsername());
                    return new InvalidCredentialsException("Invalid username or password");
                });

        if (!user.getPassword().equals(request.getPassword())) {
            log.warn("[{}] SERVICE Layer - Login failed - wrong password for user: {}", txId, request.getUsername());
            throw new InvalidCredentialsException("Invalid password");
        }

        user.setUserActive(true);
        userRepository.save(user);
        log.info("[{}] SERVICE Layer - Login successful - user '{}' is now active", txId, user.getUsername());
        return true;
    }

    @Override
    public void logout(String username) {
        String txId = MDC.get("transactionId");
        log.info("[{}] SERVICE Layer - Logging out user: {}", txId, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("[{}] SERVICE Layer - Logout failed - user not found: {}", txId, username);
                    return new NotFoundException("User not found: " + username);
                });

        user.setUserActive(false);
        userRepository.save(user);
        log.info("[{}] SERVICE Layer - Logout successful - user '{}' is now inactive", txId, username);
    }

    @Override
    public boolean isAuthenticated(String username) {
        String txId = MDC.get("transactionId");
        log.debug("[{}] SERVICE Layer - Checking isActive for user: {}", txId, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - User not found during authentication check: {}", txId, username);
                    return new NotFoundException("User not found: " + username);
                });

        boolean isAuth = user.isUserActive();
        log.debug("[{}] SERVICE Layer - User '{}' authenticated: {}", txId, username, isAuth);
        return isAuth;
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String txId = MDC.get("transactionId");
        log.info("[{}] SERVICE Layer - Changing password for user: {}", txId, request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - Password change failed - username not found: {}", txId, request.getUsername());
                    return new InvalidCredentialsException("Invalid username or password");
                });

        if (!user.getPassword().equals(request.getOldPassword())) {
            log.warn("[{}] SERVICE Layer - Password change failed - incorrect old password for user: {}", txId, request.getUsername());
            throw new InvalidCredentialsException("Old password does not match");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        log.info("[{}] SERVICE Layer - Password changed successfully for user: {}", txId, request.getUsername());
    }

    @Override
    public UserDto getUserByUsername(String targetUsername) {
        String txId = MDC.get("transactionId");
        log.debug("[{}] SERVICE Layer - Fetching user DTO by username: {}", txId, targetUsername);

        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - User not found: {}", txId, targetUsername);
                    return new NotFoundException("User not found: " + targetUsername);
                });

        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        String txId = MDC.get("transactionId");
        log.debug("[{}] SERVICE Layer - Retrieving all users", txId);
        return userMapper.toUserDtoList(userRepository.findAll());
    }

    @Override
    @Transactional
    public void deleteUser(String targetUsername) {
        String txId = MDC.get("transactionId");
        log.info("[{}] SERVICE Layer - Deleting user: {}", txId, targetUsername);

        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - Cannot delete - user not found: {}", txId, targetUsername);
                    return new NotFoundException("User not found: " + targetUsername);
                });

        userRepository.delete(user);
        log.info("[{}] SERVICE Layer - User '{}' deleted successfully", txId, targetUsername);
    }

    @Override
    @Transactional
    public void activateOrDeactivate(String targetUsername) {
        String txId = MDC.get("transactionId");
        log.info("[{}] SERVICE Layer - Toggling activation for user: {}", txId, targetUsername);

        User user = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - User not found: {}", txId, targetUsername);
                    return new NotFoundException("User not found: " + targetUsername);
                });

        user.setUserActive(!user.isUserActive());
        userRepository.save(user);
        log.info("[{}] SERVICE Layer - User '{}' is now {}", txId, targetUsername, user.isUserActive() ? "ACTIVE" : "INACTIVE");
    }

    @Override
    public User getUserEntityByUsername(String username) {
        String txId = MDC.get("transactionId");
        log.debug("[{}] SERVICE Layer - Fetching user entity by username: {}", txId, username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("[{}] SERVICE Layer - User not found: {}", txId, username);
                    return new NotFoundException("User not found: " + username);
                });
    }

    @Override
    @Transactional
    public User createUserEntity(CreateUserRequest request) {
        String txId = MDC.get("transactionId");
        log.info("[{}] SERVICE Layer - Creating new user for {} {}", txId, request.getFirstName(), request.getLastName());

        String username = credentialGenerator.generateUsername(request.getFirstName(), request.getLastName(), userRepository.findAll());
        String password = credentialGenerator.generateRandomPassword();

        User user = User.builder()
                .username(username)
                .password(password)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userActive(false)
                .build();

        User savedUser = userRepository.save(user);

        log.info("[{}] SERVICE Layer - User created with username: {}", txId, savedUser.getUsername());
        return savedUser;
    }
}
