package com.epam.springcore.controller;

import com.epam.springcore.dto.TrainingDto;
import com.epam.springcore.request.training.CreateTrainingRequest;
import com.epam.springcore.request.training.UpdateTrainingRequest;
import com.epam.springcore.service.impl.TrainingServiceImpl;
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
@RequestMapping(API_PREFIX +API_EPAM+API_VERSION_V1 + API_TRAINING)
@Tag(name = "Training Resource", description = "SpringCore REST APIs for Trainings")
public class TrainingController {

    private final TrainingServiceImpl trainingService;

    @Operation(summary = "Create Training", description = "Create a new Training")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingDto.class))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingDto createTraining(@RequestBody @Valid CreateTrainingRequest request) {
        return trainingService.createTraining(request);
    }

    @Operation(summary = "Get Training by ID", description = "Retrieve a Training by ID")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingDto.class))))
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrainingDto getTraining(@PathVariable Long id) {
        return trainingService.getTraining(id);
    }

    @Operation(summary = "List All Trainings", description = "Get all trainings")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingDto.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TrainingDto> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    @Operation(summary = "Update Training", description = "Update training details")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingDto.class))))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrainingDto updateTraining(@PathVariable Long id, @RequestBody @Valid UpdateTrainingRequest request) {
        return trainingService.updateTraining(id, request);
    }

    @Operation(summary = "Delete Training", description = "Delete a training by ID")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "204",
                    description = "No Content"))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTraining(@PathVariable Long id) {
        trainingService.deleteTraining(id);
    }
}
