package com.epam.springcore.controller;

import com.epam.springcore.dto.TrainerDto;
import com.epam.springcore.request.create.CreateTrainerRequest;
import com.epam.springcore.service.ITrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.springcore.constants.Constant.*;

@RestController
@RequestMapping(API_PREFIX +API_EPAM+ API_VERSION_V1 + API_TRAINER)
@Tag(name = "Trainer Resource", description = "SpringCore REST APIs for Trainers")
public class TrainerController {

    private final ITrainerService trainerService;
    @Autowired
    public TrainerController(ITrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Operation(summary = "Create Trainer", description = "Create a new Trainer")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "201",
                    description = "Created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerDto.class))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainerDto createTrainer(@RequestBody @Valid CreateTrainerRequest request) {
        return trainerService.createTrainer(request);
    }

    @Operation(summary = "Get Trainer by ID", description = "Retrieve a Trainer by ID")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerDto.class))))
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrainerDto getTrainer(@PathVariable String id) {
        return trainerService.getTrainer(id);
    }

    @Operation(summary = "List All Trainers", description = "Get all trainers")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerDto.class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TrainerDto> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    @Operation(summary = "Update Trainer", description = "Update trainer details")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerDto.class))))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TrainerDto updateTrainer(@PathVariable String id, @RequestBody @Valid CreateTrainerRequest request) {
        return trainerService.updateTrainer(id, request);
    }

    @Operation(summary = "Delete Trainer", description = "Delete a trainer by ID")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "204",
                    description = "No Content"))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainer(@PathVariable String id) {
        trainerService.deleteTrainer(id);
    }
}
