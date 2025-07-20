package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.UserDto;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.user.LoginRequest;
import com.epam.springcore.request.user.UpdatePasswordRequest;
import com.epam.springcore.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.springcore.constants.Constant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PREFIX + API_EPAM + API_VERSION_V1 + API_USER)
@Tag(name = "User Resource", description = "SpringCore REST APIs for Users")
public class UserController {

    private final IUserService userService;

    @Operation(summary = "Get user by username", description = "Fetch a user by their username")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))))
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @Operation(summary = "Get all users", description = "Retrieve all users")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Delete user", description = "Delete a user by username")
    @ApiResponses(
            @ApiResponse(responseCode = "204", description = "No Content"))
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
    }

    @Operation(summary = "Update password", description = "Update the password of a user")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))))
    @PutMapping("/{username}/password")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updatePassword(@PathVariable String username, @RequestBody @Valid UpdatePasswordRequest request) {
        return userService.updatePassword(username, request);
    }

    @Operation(summary = "Login", description = "Authenticate a user with username and password")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))))
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public boolean login(@RequestBody @Valid LoginRequest request) {
        return userService.login(request);
    }

    @Operation(summary = "Create new trainee", description = "Create a new Trainee with user details")
    @ApiResponses(
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TraineeDto.class))))
    @PostMapping("/trainee")
    @ResponseStatus(HttpStatus.CREATED)
    public TraineeDto createTrainee(@RequestBody @Valid CreateTraineeRequest request) {
        return userService.createTrainee(request);
    }

    @Operation(summary = "Create new trainer", description = "Create a new Trainer with user details")
    @ApiResponses(
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerDto.class))))
    @PostMapping("/trainer")
    @ResponseStatus(HttpStatus.CREATED)
    public TrainerDto createTrainer(@RequestBody @Valid CreateTrainerRequest request) {
        return userService.createTrainer(request);
    }

    @Operation(summary = "Activate or deactivate user", description = "Toggle the activation status of a user")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "OK"))
    @PatchMapping("/{username}/toggle-activation")
    @ResponseStatus(HttpStatus.OK)
    public void toggleActivation(@PathVariable String username) {
        userService.activateOrDeactivate(username);
    }
}
