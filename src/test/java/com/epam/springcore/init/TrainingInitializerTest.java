package com.epam.springcore.init;

import com.epam.springcore.dao.TrainingDao;
import com.epam.springcore.model.Training;
import com.epam.springcore.model.enums.TrainingType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class TrainingInitializerTest {

    private ObjectMapper objectMapper;
    private TrainingDao trainingDao;
    private Resource trainingDataFile;
    private TrainingInitializer trainingInitializer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        trainingDao = mock(TrainingDao.class);
        trainingDataFile = mock(Resource.class);
        trainingInitializer = new TrainingInitializer(objectMapper, trainingDao);

        try {
            var field = TrainingInitializer.class.getDeclaredField("trainingDataFile");
            field.setAccessible(true);
            field.set(trainingInitializer, trainingDataFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Positive Scenario - Training data is read and saved")
    void testInit_Success() throws Exception {
        String jsonData = """
            [
              {
                "traineeId": "1",
                "trainerId": "6",
                "date": "2025-07-11",
                "type": "Strength",
                "durationMinutes": 60
              }
            ]
        """;

        when(trainingDataFile.getInputStream()).thenReturn(
                new ByteArrayInputStream(jsonData.getBytes(StandardCharsets.UTF_8))
        );

        trainingInitializer.init();

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDao, times(1)).save(captor.capture());

        Training training = captor.getValue();
        assertThat(training.getTraineeId()).isEqualTo("1");
        assertThat(training.getTrainerId()).isEqualTo("6");
        assertThat(training.getType()).isEqualTo(TrainingType.STRENGTH);
        assertThat(training.getDurationMinutes()).isEqualTo(60);
    }

    @Test
    @DisplayName("Negative Scenario - JSON parse failure is handled")
    void testInit_InvalidJson_ShouldHandleException() throws Exception {
        String invalidJson = "INVALID_JSON";
        when(trainingDataFile.getInputStream()).thenReturn(
                new ByteArrayInputStream(invalidJson.getBytes(StandardCharsets.UTF_8))
        );

        assertThatCode(() -> trainingInitializer.init())
                .doesNotThrowAnyException();

        verify(trainingDao, never()).save(any());
    }
}