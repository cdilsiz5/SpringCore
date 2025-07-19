package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.request.trainee.CreateTraineeRequest;
import com.epam.springcore.service.ITraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.epam.springcore.constants.Constant.*;


@RestController
@RequestMapping(API_PREFIX + API_EPAM+API_VERSION_V1 +API_TRAINEE )
@Tag(name = "Trainee Resource", description = "SpringCore REST APIs for Trainees")
public class TraineeController {

    private final ITraineeService traineeService;

    public TraineeController(ITraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK",
                    content = @Content(
                            schema = @Schema(implementation = TraineeDto.class),
                            mediaType = "application/json")))
    @Operation(summary = "Get Trainee by ID", description = "Retrieve a Trainee by ID")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TraineeDto getTrainee(@PathVariable String id) {
        return traineeService.getTrainee(id);
    }

    @Operation(summary = "List All Trainees", description = "Get all trainees")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK",
                    content = @Content(
                            schema = @Schema(implementation = TraineeDto.class),
                            mediaType = "application/json")))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TraineeDto> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    @Operation(summary = "Update Trainee", description = "Update trainee details")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK",
                    content = @Content(
                            schema = @Schema(implementation = TraineeDto.class),
                            mediaType = "application/json")))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TraineeDto updateTrainee(@PathVariable String id, @RequestBody @Valid CreateTraineeRequest request) {
        return traineeService.updateTrainee(id, request);
    }

    @Operation(summary = "Delete Trainee", description = "Delete a trainee by ID")
    @ApiResponses(
            @ApiResponse(
                    responseCode = "204",
                    description = "HTTP Status NO CONTENT"))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainee(@PathVariable String id) {
        traineeService.deleteTrainee(id);
    }
}
