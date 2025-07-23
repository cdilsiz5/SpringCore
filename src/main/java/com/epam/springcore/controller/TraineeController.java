package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.service.impl.TraineeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/epam/v1/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainee Controller", description = "Endpoints for managing trainees")
public class TraineeController {

    private final TraineeServiceImpl traineeService;

    @Operation(summary = "Create new trainee", description = "Public endpoint. Creates a new trainee and generates credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TraineeDto> createTrainee(@RequestBody @Valid CreateTraineeRequest request) {
        return ResponseEntity.ok(traineeService.createTrainee(request));
    }

    @Operation(summary = "Get trainee by username", description = "Authenticated endpoint. Retrieves a specific trainee by username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}")
    public ResponseEntity<TraineeDto> getTraineeByUsername(
            @RequestHeader("authUsername") String authUsername,
            @RequestHeader("authPassword") String authPassword,
            @PathVariable String username) {
        return ResponseEntity.ok(traineeService.getTraineeByUsername(authUsername,authPassword,username));
    }

    @Operation(summary = "Get all trainees", description = "Authenticated endpoint. Retrieves all trainees.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainees fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<TraineeDto>> getAllTrainees(
            @RequestHeader("authUsername") String authUsername,
            @RequestHeader("authPassword") String authPassword) {
        return ResponseEntity.ok(traineeService.getAllTrainees(authUsername, authPassword));
    }

    @Operation(summary = "Update trainee", description = "Authenticated endpoint. Updates trainee's information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping("/{username}")
    public ResponseEntity<TraineeDto> updateTrainee(
            @RequestHeader("authUsername") String authUsername,
            @RequestHeader("authPassword") String authPassword,
            @PathVariable String username,
            @RequestBody @Valid UpdateTraineeRequest request) {
        return ResponseEntity.ok(traineeService.updateTrainee(authUsername, authPassword,username, request));
    }

    @Operation(summary = "Delete trainee", description = "Authenticated endpoint. Deletes the trainee account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(
            @RequestHeader("authUsername") String authUsername,
            @RequestHeader("authPassword") String authPassword,
            @PathVariable String username) {
        traineeService.deleteTrainee(authUsername,authPassword,username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Toggle trainee activation", description = "Authenticated endpoint. Toggles trainee's active status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activation status toggled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PatchMapping("/{username}/toggle-activation")
    public ResponseEntity<Void> toggleTraineeActivation(
            @RequestHeader("authUsername") String authUsername,
            @RequestHeader("authPassword") String authPassword,
            @PathVariable String username) {
        traineeService.toggleActivation(authUsername, authPassword,username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get training history", description = "Authenticated endpoint. Retrieves trainee's training history with optional filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training history fetched"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/{username}/training-history")
    public ResponseEntity<List<TrainingDto>> getTrainingHistory(
            @RequestHeader("authUsername") String authUsername,
            @RequestHeader("authPassword") String authPassword,
            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainerLastName) {

        return ResponseEntity.ok(
                traineeService.getTrainingHistory(username, authPassword,username, from, to, trainerName, trainerLastName)
        );
    }
}
