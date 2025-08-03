package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainer.TrainerUsernameRequest;
import com.epam.springcore.response.LoginCredentialsResponse;
import com.epam.springcore.service.ITraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/epam/v1/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainee Controller", description = "Endpoints for managing trainees")
public class TraineeController {

    private final ITraineeService traineeService;

    @Operation(summary = "Create new trainee", description = "Public endpoint. Creates a new trainee and generates credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping
    public ResponseEntity<LoginCredentialsResponse> createTrainee(@RequestBody @Valid CreateTraineeRequest request) {
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

            @PathVariable String username) {
        return ResponseEntity.ok(traineeService.getTraineeByUsername(username));
    }

    @Operation(summary = "Get all trainees", description = "Authenticated endpoint. Retrieves all trainees.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainees fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<TraineeDto>> getAllTrainees() {
        return ResponseEntity.ok(traineeService.getAllTrainees());
    }

    @Operation(summary = "Update trainee", description = "Authenticated endpoint. Updates trainee's information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping("/{username}")
    public ResponseEntity<TraineeDto> updateTrainee(

            @PathVariable String username,
            @RequestBody @Valid UpdateTraineeRequest request) {
        return ResponseEntity.ok(traineeService.updateTrainee(username, request));
    }

    @Operation(summary = "Delete trainee", description = "Authenticated endpoint. Deletes the trainee account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(

            @PathVariable String username) {
        traineeService.deleteTrainee(username);
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

            @PathVariable String username) {
        traineeService.toggleActivation(username);
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

            @PathVariable String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainerLastName) {

        return ResponseEntity.ok(
                traineeService.getTrainingHistory(username, from, to, trainerName, trainerLastName)
        );
    }
    @Operation(summary = "Get unassigned trainers", description = "List of trainers with no training assigned to this trainee")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainerDto.class))))
    @GetMapping("/{username}/unassigned-trainers")
    @ResponseStatus(HttpStatus.OK)
    public List<TrainerDto> getUnassignedTrainers(@PathVariable String username) {
        return traineeService.getUnassignedTrainers(username);
    }
    @Operation(summary = "Update trainer list", description = "Assign or update trainers for a trainee")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Trainer list updated"))
    @PutMapping("/{username}/update-trainer-list")
    @ResponseStatus(HttpStatus.OK)
    public void updateTrainerList(
            @PathVariable String username,
            @RequestBody List<TrainerUsernameRequest> trainers
    ) {
        traineeService.updateTrainerList(username, trainers);
    }


}

