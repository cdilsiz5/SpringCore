package com.epam.springcore.init;

import com.epam.springcore.dao.TrainingDao;
import com.epam.springcore.model.Training;
import com.epam.springcore.model.enums.TrainingType;
import com.epam.springcore.request.init.TrainingInitializeRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TrainingInitializerTest {

    private ObjectMapper objectMapper;
    private TrainingDao trainingDao;
    private Resource trainingDataFile;
    private TrainingInitializer trainingInitializer;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = mock(ObjectMapper.class);
        trainingDao = mock(TrainingDao.class);
        trainingDataFile = mock(Resource.class);
        trainingInitializer = new TrainingInitializer(objectMapper, trainingDao);

        Field field = TrainingInitializer.class.getDeclaredField("trainingDataFile");
        field.setAccessible(true);
        field.set(trainingInitializer, trainingDataFile);
    }

    @Test
    @DisplayName("Success - Should read and save training data")
    void testInit_Success() throws Exception {
        String json = """
            [
              {
                "traineeId": "1",
                "trainerId": "6",
                "date": "2025-08-01",
                "type": "CROSSFIT",
                "durationMinutes": 60
              }
            ]
        """;

        InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        when(trainingDataFile.getInputStream()).thenReturn(inputStream);

        List<TrainingInitializeRequest> mockList = List.of(
                new TrainingInitializeRequest("1", "6", "2025-08-01", "CROSSFIT", 60)
        );
        when(objectMapper.readValue(any(InputStream.class),
                ArgumentMatchers.<TypeReference<List<TrainingInitializeRequest>>>any()))
                .thenReturn(mockList);

        trainingInitializer.init();

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDao, times(1)).save(captor.capture());

        Training saved = captor.getValue();
        assertThat(saved.getTraineeId()).isEqualTo("1");
        assertThat(saved.getTrainerId()).isEqualTo("6");
        assertThat(saved.getType()).isEqualTo(TrainingType.CROSSFIT);
        assertThat(saved.getDurationMinutes()).isEqualTo(60);
    }

    @Test
    @DisplayName("JSON Parse Fails - Should handle error gracefully")
    void testInit_WhenJsonFails_ShouldHandleGracefully() throws Exception {
        when(trainingDataFile.getInputStream()).thenReturn(
                new ByteArrayInputStream("INVALID_JSON".getBytes(StandardCharsets.UTF_8))
        );

        when(objectMapper.readValue(any(InputStream.class),
                ArgumentMatchers.<TypeReference<List<TrainingInitializeRequest>>>any()))
                .thenThrow(new RuntimeException("Parse error"));

        assertThatCode(() -> trainingInitializer.init())
                .doesNotThrowAnyException();

        verify(trainingDao, never()).save(any());
    }

    @Test
    @DisplayName("Empty List - Should not save any training")
    void testInit_EmptyList_ShouldNotSave() throws Exception {
        when(trainingDataFile.getInputStream()).thenReturn(
                new ByteArrayInputStream("[]".getBytes(StandardCharsets.UTF_8))
        );

        when(objectMapper.readValue(any(InputStream.class),
                ArgumentMatchers.<TypeReference<List<TrainingInitializeRequest>>>any()))
                .thenReturn(List.of());

        trainingInitializer.init();

        verify(trainingDao, never()).save(any());
    }
}
