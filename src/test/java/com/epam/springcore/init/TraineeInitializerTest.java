package com.epam.springcore.init;

import com.epam.springcore.model.enums.TrainingType;
import com.epam.springcore.request.TraineeInitializeRequest;
import com.epam.springcore.service.ITraineeService;
import com.epam.springcore.service.IUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class TraineeInitializerTest {

    @Mock
    private ITraineeService traineeService;

    @Mock
    private IUserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Resource traineeDataFile;

    @InjectMocks
    private TraineeInitializer traineeInitializer;

    private final String mockJson = "[{\"firstName\":\"Ali\",\"lastName\":\"Yılmaz\",\"dateOfBirth\":\"1995-05-10\",\"address\":\"Ankara\"}]";

    @BeforeEach
    void setUp() throws Exception {
        openMocks(this);

        InputStream mockInputStream = new ByteArrayInputStream(mockJson.getBytes(StandardCharsets.UTF_8));
        when(traineeDataFile.getInputStream()).thenReturn(mockInputStream);

        Field field = TraineeInitializer.class.getDeclaredField("traineeDataFile");
        field.setAccessible(true);
        field.set(traineeInitializer, traineeDataFile);
    }

    @Test
    @DisplayName("Positive - JSON processed and services called")
    void testInitShouldCallUserAndTraineeServices() throws Exception {
        List<TraineeInitializeRequest> mockList = List.of(
                new TraineeInitializeRequest("Ali", "Yılmaz", "1995-05-10", "Ankara")
        );

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockList);

        traineeInitializer.init();

        verify(userService, times(1)).save(any());
        verify(traineeService, times(1)).createTrainee(any());
    }

    @Test
    @DisplayName("Negative - JSON parsing fails")
    void testInit_WhenJsonParseFails_ShouldNotCallServices() throws Exception {
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenThrow(new IOException("Invalid JSON"));

        traineeInitializer.init();

        verify(userService, never()).save(any());
        verify(traineeService, never()).createTrainee(any());
    }

    @Test
    @DisplayName("Negative - InputStream fails")
    void testInit_WhenInputStreamFails_ShouldNotCallServices() throws Exception {
        when(traineeDataFile.getInputStream())
                .thenThrow(new IOException("File not found"));

        traineeInitializer.init();

        verify(userService, never()).save(any());
        verify(traineeService, never()).createTrainee(any());
    }

    @Test
    @DisplayName("Edge Case - Empty JSON list")
    void testInit_WhenJsonListEmpty_ShouldNotCallServices() throws Exception {
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(List.of());

        traineeInitializer.init();

        verify(userService, never()).save(any());
        verify(traineeService, never()).createTrainee(any());
    }
}
