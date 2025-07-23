package com.epam.springcore.controller;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.request.trainer.CreateTrainerRequest;
import com.epam.springcore.request.trainer.UpdateTrainerRequest;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.epam.springcore.constants.Constant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PREFIX + API_EPAM + API_VERSION_V1 + API_TRAINER)
@Tag(name = "Trainer Resource", description = "SpringCore REST APIs for Trainers")
public class TrainerController {

    private final TrainerServiceImpl trainerService;

    @Operation(summary = "Create trainer (Public)", description = "Creates a new trainer")
    @ApiResponses(@ApiResponse(responseCode = "201", description = "Created",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainerDto.class))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainerDto createTrainer(@RequestBody @Valid CreateTrainerRequest request) {
        return trainerService.createTrainer(request);
    }

    @Operation(summary = "Get trainer by username", description = "Fetch a trainer by username")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainerDto.class))))
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public TrainerDto getTrainerByUsername(@PathVariable String username,
                                           @RequestHeader("X-Username") String authUser,
                                           @RequestHeader("X-Password") String authPass) {
        return trainerService.getTrainerByUsername(authUser, authPass, username);
    }

    @Operation(summary = "Get all trainers", description = "Retrieve all trainers")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainerDto.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TrainerDto> getAllTrainers(@RequestHeader("X-Username") String authUser,
                                           @RequestHeader("X-Password") String authPass) {
        return trainerService.getAllTrainers(authUser, authPass);
    }

    @Operation(summary = "Update trainer", description = "Update trainer details")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Updated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TrainerDto.class))))
    @PutMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public TrainerDto updateTrainer(@PathVariable String username,
                                    @RequestHeader("X-Username") String authUser,
                                    @RequestHeader("X-Password") String authPass,
                                    @RequestBody @Valid UpdateTrainerRequest request) {
        return trainerService.updateTrainer(authUser, authPass, username, request);
    }

    @Operation(summary = "Delete trainer", description = "Delete a trainer by username")
    @ApiResponses(@ApiResponse(responseCode = "204", description = "No Content"))
    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainer(@PathVariable String username,
                              @RequestHeader("X-Username") String authUser,
                              @RequestHeader("X-Password") String authPass) {
        trainerService.deleteTrainer(authUser, authPass, username);
    }

    @Operation(summary = "Toggle trainer activation", description = "Activate/deactivate trainer account")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Status toggled"))
    @PatchMapping("/{username}/toggle-activation")
    @ResponseStatus(HttpStatus.OK)
    public void toggleActivation(@PathVariable String username,
                                 @RequestHeader("X-Username") String authUser,
                                 @RequestHeader("X-Password") String authPass) {
        trainerService.toggleActivation(authUser, authPass, username);
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
            @RequestHeader("X-Username") String authUser,
            @RequestHeader("X-Password") String authPass,
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
        return trainerService.getTrainingHistory(authUser, authPass, username, from, to, traineeName, traineeLastName);
    }

}
