package com.epam.springcore.init;

import com.epam.springcore.model.enums.TrainingType;
import com.epam.springcore.request.TrainerInitializeRequest;
import com.epam.springcore.service.ITrainerService;
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

public class TrainerInitializerTest {

    @Mock
    private ITrainerService trainerService;

    @Mock
    private IUserService userService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Resource trainerDataFile;

    @InjectMocks
    private TrainerInitializer trainerInitializer;

    private final String mockJson = "[{\"firstName\":\"Ahmet\",\"lastName\":\"Kaya\",\"specialty\":\"CROSSFIT\"}]";

    @BeforeEach
    void setUp() throws Exception {
        openMocks(this);

        InputStream mockInputStream = new ByteArrayInputStream(mockJson.getBytes(StandardCharsets.UTF_8));
        when(trainerDataFile.getInputStream()).thenReturn(mockInputStream);

        Field field = TrainerInitializer.class.getDeclaredField("trainerDataFile");
        field.setAccessible(true);
        field.set(trainerInitializer, trainerDataFile);

        when(trainerService.getAllTrainers()).thenReturn(List.of());
    }

    @Test
    @DisplayName("Positive - JSON parse success, services called")
    void testInitShouldCallUserAndTrainerServices() throws Exception {
        List<TrainerInitializeRequest> mockList = List.of(
                new TrainerInitializeRequest("Ahmet", "Kaya", TrainingType.CROSSFIT)
        );

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockList);

        trainerInitializer.init();

        verify(userService, times(1)).save(any());
        verify(trainerService, times(1)).createTrainer(any());
    }

    @Test
    @DisplayName("Negative - JSON parsing throws exception")
    void testInit_WhenJsonParseFails_ShouldNotCallServices() throws Exception {
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenThrow(new IOException("Failed to parse"));

        trainerInitializer.init();

        verify(userService, never()).save(any());
        verify(trainerService, never()).createTrainer(any());
    }

    @Test
    @DisplayName("Negative - InputStream throws IOException")
    void testInit_WhenInputStreamFails_ShouldNotCallServices() throws Exception {
        when(trainerDataFile.getInputStream()).thenThrow(new IOException("Can't read file"));

        trainerInitializer.init();

        verify(userService, never()).save(any());
        verify(trainerService, never()).createTrainer(any());
    }

    @Test
    @DisplayName("Edge Case - Empty trainer list in JSON")
    void testInit_WhenJsonListIsEmpty_ShouldNotCallServices() throws Exception {
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(List.of());

        trainerInitializer.init();

        verify(userService, never()).save(any());
        verify(trainerService, never()).createTrainer(any());
    }
}
