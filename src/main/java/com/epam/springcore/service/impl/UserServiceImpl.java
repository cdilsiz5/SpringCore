package com.epam.springcore.service.impl;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.UserDto;
import com.epam.springcore.exception.InvalidCredentialsException;
import com.epam.springcore.exception.NotFoundException;
import com.epam.springcore.mapper.UserMapper;
import com.epam.springcore.model.User;
import com.epam.springcore.repository.UserRepository;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.user.CreateUserRequest;
import com.epam.springcore.request.user.LoginRequest;
import com.epam.springcore.request.user.UpdatePasswordRequest;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.service.IUserService;
import com.epam.springcore.session.UserSessionRegistry;
import com.epam.springcore.util.CredentialGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final ITrainerService trainerService;
    private final ITraineeService traineeService;
    private final CredentialGenerator credentialGenerator;
    private final UserMapper userMapper;
    private final UserSessionRegistry  userSessionRegistry;

    @Override
    public UserDto getUserByUsername(String username) {
        log.info("Fetching user with username: {}", username);
        User user = checkIfUserExistByUsername(username);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Fetching all users");
        return userMapper.toUserDtoList(userRepository.findAll());
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
        log.info("old password : {}", user.getPassword());
        user.setPassword(request.getNewPassword());
        User updatedUser = userRepository.save(user);
        log.info(updatedUser.toString());
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public boolean login(LoginRequest request) {
        log.info("Attempting login for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found with username {}", request.getUsername());
                    return new NotFoundException("User Not Found with username: " + request.getUsername());
                });

        if (!user.getPassword().equals(request.getPassword())) {
            log.warn("Login failed: Incorrect password for username {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (!user.isUserActive()) {
            log.warn("Login failed: User is not active: {}", request.getUsername());
            throw new InvalidCredentialsException("User is not active");
        }

        log.info("Login successful for username: {}", request.getUsername());
        userSessionRegistry.setActive(user.getUsername());
        return true;
    }

    @Override
    @Transactional
    public TraineeDto createTrainee(CreateTraineeRequest request) {
        log.info("Creating User for Trainee: {} {}", request.getFirstName(), request.getLastName());

        CreateUserRequest userRequest = CreateUserRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User savedUser = createUserEntity(userRequest);
        log.info("User created for Trainee. User ID: {}", savedUser.getId());

        return traineeService.createTraineeEntity(savedUser, request.getDateOfBirth(), request.getAddress());
    }

    @Override
    @Transactional
    public TrainerDto createTrainer(CreateTrainerRequest request) {
        log.info("Creating User for Trainer: {} {}", request.getFirstName(), request.getLastName());

        CreateUserRequest userRequest = CreateUserRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User savedUser = createUserEntity(userRequest);
        log.info("User created for Trainer. User ID: {}", savedUser.getId());

        return trainerService.createTrainerEntity(savedUser, request.getSpecialty());
    }
    @Override
    public void logout(String username) {

        if (userSessionRegistry.isActive(username)) {
            userSessionRegistry.removeUser(username);
            log.info("User '{}' has been logged out and removed from session registry.", username);
        } else {
            log.warn("Logout requested for username '{}', but no active session was found.", username);
        }
    }

    private User checkIfUserExistByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with username %s not found", username)
                ));
    }
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    public void activateOrDeactivate(String username) {
        log.info("Toggling activation status for user with username: {}", username);
        User user = getUserEntityByUsername(username);
        user.setUserActive(!user.isUserActive());
        log.info("User activation status for '{}' changed to {}", username, user.isUserActive());
        userRepository.save(user);
     }
    private User createUserEntity(CreateUserRequest request) {
        log.info("Creating user with Name : {}", request.getFirstName()+" "+request.getLastName());
        User user = userMapper.createUser(request);
        user.setUsername(credentialGenerator.generateUsername(request.getFirstName(), request.getLastName(),userRepository.findAll()));
        user.setPassword(credentialGenerator.generateRandomPassword());
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUsername());
        return  savedUser;
    }



}
