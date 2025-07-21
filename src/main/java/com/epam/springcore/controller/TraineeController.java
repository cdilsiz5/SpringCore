package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.request.trainee.UpdateTraineeRequest;
import com.epam.springcore.request.trainee.UpdateTraineeTrainerListRequest;
import com.epam.springcore.service.ITraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.epam.springcore.constants.Constant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PREFIX + API_EPAM + API_VERSION_V1 + API_TRAINEE)
@Tag(name = "Trainee Resource", description = "SpringCore REST APIs for Trainees")
public class TraineeController {

    private final ITraineeService traineeService;

    @Operation(summary = "Get trainee by username", description = "Fetch a trainee by username")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TraineeDto.class))))
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public TraineeDto getTraineeByUsername(@PathVariable String username) {
        return traineeService.getTraineeByUsername(username);
    }

    @Operation(summary = "Get all trainees", description = "Retrieve all trainees")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TraineeDto.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TraineeDto> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    @Operation(summary = "Update trainee", description = "Update trainee details by username")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TraineeDto.class))))
    @PutMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public TraineeDto updateTrainee(@PathVariable String username,
                                    @RequestBody @Valid UpdateTraineeRequest request) {
        return traineeService.updateTrainee(username, request);
    }

    @Operation(summary = "Delete trainee", description = "Delete trainee by username")
    @ApiResponses(@ApiResponse(responseCode = "204", description = "No Content"))
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainee(@PathVariable String username) {
        traineeService.deleteTrainee(username);
    }

    @Operation(summary = "Update assigned trainers", description = "Update list of trainers assigned to a trainee")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TraineeDto.class))))
    @PutMapping("/{username}/trainers")
    @ResponseStatus(HttpStatus.OK)
    public TraineeDto updateTrainerList(@PathVariable String username,
                                        @RequestBody @Valid UpdateTraineeTrainerListRequest request) {
        return traineeService.updateTrainerList(username, request);
    }

    @Operation(summary = "Get training history", description = "Get training history for a trainee with optional filters")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainingDto.class))))
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
            @Parameter(description = "Trainer First Name", example = "Ayşe")
            String trainerName,
            @RequestParam(required = false)
            @Parameter(description = "Trainer Last Name", example = "Yılmaz")
            String trainerLastname,
            @RequestParam(required = false)
            @Parameter(description = "Training Type", example = "CARDIO")
            String trainingType
    ) {
        return traineeService.getTrainingHistory(username,
                from != null ? from.toString() : null,
                to != null ? to.toString() : null,
                trainerName,
                trainerLastname,
                trainingType
        );
    }

    @Operation(summary = "Get unassigned trainers", description = "List trainers not assigned to the trainee ")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainerDto.class))))
    @GetMapping("/{username}/unassigned-trainers")
    @ResponseStatus(HttpStatus.OK)
    public List<TrainerDto> getUnassignedTrainers(@PathVariable String username) {
        return traineeService.getUnassignedTrainers(username);
    }
}
