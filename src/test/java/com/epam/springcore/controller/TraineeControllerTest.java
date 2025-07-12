package com.epam.springcore.controller;

import com.epam.springcore.dto.TraineeDto;
import com.epam.springcore.request.CreateTraineeRequest;
import com.epam.springcore.service.ITraineeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITraineeService traineeService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_URL = "/api/epam/v1/trainee";

    private final TraineeDto sampleDto = new TraineeDto(
            "id123", "Ali", "Yılmaz", LocalDate.of(2000, 1, 1), "Ankara"
    );



    @Test
    @DisplayName("POST /api/epam/v1/trainee - debug mode")
    void debug_createTrainee_shouldPrintResponse() throws Exception {
        CreateTraineeRequest request = new CreateTraineeRequest();
        request.setFirstName("Ali");
        request.setLastName("Yılmaz");
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));
        request.setAddress("Ankara");

        TraineeDto mockDto = new TraineeDto("id123", "Ali", "Yılmaz", LocalDate.of(2000, 1, 1), "Ankara");

        Mockito.when(traineeService.createTrainee(any())).thenReturn(mockDto);

        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

    }

    @Test
    @DisplayName("DELETE /api/epam/v1/trainee/{id} - should delete trainee")
    void deleteTrainee_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/id123"))
                .andExpect(status().isNoContent());

        verify(traineeService, times(1)).deleteTrainee("id123");
    }
}
