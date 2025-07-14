package com.epam.springcore.init;

import com.epam.springcore.dao.TrainerDao;
import com.epam.springcore.dao.UserDao;
import com.epam.springcore.model.enums.TrainingType;
import com.epam.springcore.request.init.TrainerInitializeRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentMatchers;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class TrainerInitializerTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private UserDao userDao;

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

        when(trainerDao.findAll()).thenReturn(List.of());
    }

    @Test
    @DisplayName(" Success - JSON parsed and trainers initialized")
    void testInit_Success_ShouldCallDaoMethods() throws Exception {
        List<TrainerInitializeRequest> mockList = List.of(
                new TrainerInitializeRequest("Ahmet", "Kaya", TrainingType.CROSSFIT),
                new TrainerInitializeRequest("Cihan", "Dilsiz", TrainingType.BOXING)
        );

        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<TrainerInitializeRequest>>>any()))
                .thenReturn(mockList);

        trainerInitializer.init();
        verify(userDao, times(2)).save(any());
        verify(trainerDao, times(2)).save(any());
    }

    @Test
    @DisplayName(" JSON parsing throws exception")
    void testInit_WhenJsonParseFails_ShouldNotCallDaos() throws Exception {
        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<TrainerInitializeRequest>>>any()))
                .thenThrow(new RuntimeException("Failed to parse"));

        trainerInitializer.init();

        verify(userDao, never()).save(any());
        verify(trainerDao, never()).save(any());
    }

    @Test
    @DisplayName(" InputStream throws exception")
    void testInit_WhenInputStreamFails_ShouldNotCallDaos() throws Exception {
        when(trainerDataFile.getInputStream()).thenThrow(new RuntimeException("I/O Error"));

        trainerInitializer.init();

        verify(userDao, never()).save(any());
        verify(trainerDao, never()).save(any());
    }

    @Test
    @DisplayName(" Empty list - Should not call save")
    void testInit_WhenJsonListEmpty_ShouldNotCallDaos() throws Exception {
        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<TrainerInitializeRequest>>>any()))
                .thenReturn(List.of());

        trainerInitializer.init();

        verify(userDao, never()).save(any());
        verify(trainerDao, never()).save(any());
    }
}
