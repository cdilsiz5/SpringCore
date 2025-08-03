package com.epam.springcore.controller;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;
import com.epam.springcore.response.LoginCredentialsResponse;
import com.epam.springcore.service.ITrainerService;
import com.epam.springcore.service.impl.TrainerServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequiredArgsConstructor
@RequestMapping("/api/epam/v1/trainers")
@Tag(name = "Trainer Resource", description = "SpringCore REST APIs for Trainers")
public class TrainerController {

    private final ITrainerService trainerService;

    @Operation(summary = "Create trainer (Public)", description = "Creates a new trainer")
    @ApiResponses(@ApiResponse(responseCode = "201", description = "Created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainerDto.class))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LoginCredentialsResponse> createTrainer(@RequestBody @Valid CreateTrainerRequest request) {
        return ResponseEntity.ok(trainerService.createTrainer(request));
    }

    @Operation(summary = "Get trainer by username", description = "Fetch a trainer by username")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainerDto.class))))
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public TrainerDto getTrainerByUsername(@PathVariable String username) {
        return trainerService.getTrainerByUsername( username);
    }

    @Operation(summary = "Get all trainers", description = "Retrieve all trainers")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainerDto.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TrainerDto> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    @Operation(summary = "Update trainer", description = "Update trainer details")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Updated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainerDto.class))))
    @PutMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public TrainerDto updateTrainer(@PathVariable String username,

                                    @RequestBody @Valid UpdateTrainerRequest request) {
        return trainerService.updateTrainer( username, request);
    }

    @Operation(summary = "Delete trainer", description = "Delete a trainer by username")
    @ApiResponses(@ApiResponse(responseCode = "204", description = "No Content"))
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainer(@PathVariable String username) {
        trainerService.deleteTrainer(username);
    }

    @Operation(summary = "Toggle trainer activation", description = "Activate/deactivate trainer account")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Status toggled"))
    @PatchMapping("/{username}/toggle-activation")
    @ResponseStatus(HttpStatus.OK)
    public void toggleActivation(@PathVariable String username) {
        trainerService.toggleActivation(username);
    }


    @Operation(
            summary = "Get training history",
            description = "Returns a list of trainings for a user with optional filters (from, to, traineeName, traineeLastName)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingDto.class)))
    })
    @GetMapping("/{username}/trainings")
    @ResponseStatus(HttpStatus.OK)
    public List<TrainingDto> getTrainingHistory(
            @PathVariable String username,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Start date (yyyy-MM-dd)", example = "2024-01-01")
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "End date (yyyy-MM-dd)", example = "2024-12-31")
            LocalDate to,
            @RequestParam(required = false)
            @Parameter(description = "Trainee First Name", example = "Ali")
            String traineeName,
            @RequestParam(required = false)
            @Parameter(description = "Trainee Last Name", example = "Veli")
            String traineeLastName
    ) {
        return trainerService.getTrainingHistory( username, from, to, traineeName, traineeLastName);
    }



}
