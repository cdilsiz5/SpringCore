package com.epam.springcore.init;

import com.epam.springcore.dao.TraineeDao;
import com.epam.springcore.dao.UserDao;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.User;
import com.epam.springcore.request.init.TraineeInitializeRequest;
import com.epam.springcore.service.ITraineeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class TraineeInitializerTest {

    @Mock
    private ITraineeService traineeService;

    @Mock
    private UserDao userDao;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Resource traineeDataFile;

    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TraineeInitializer traineeInitializer;

    private final String mockJson = """
        [
          {
            "firstName": "Ali",
            "lastName": "Yılmaz",
            "dateOfBirth": "1995-05-10",
            "address": "Ankara"
          }
        ]
    """;

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
    @DisplayName("Success - Should parse JSON and call DAO methods")
    void testInit_Success_ShouldCallDaoMethods() throws Exception {
        TraineeInitializeRequest expectedRequest = new TraineeInitializeRequest(
                "Ali", "Yılmaz", LocalDate.of(1995, 5, 10), "Ankara"
        );

        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<TraineeInitializeRequest>>>any()))
                .thenReturn(List.of(expectedRequest));

        when(traineeDao.findAll()).thenReturn(List.of());

        traineeInitializer.init();

        verify(userDao, times(1)).save(any(User.class));
        verify(traineeDao, times(1)).save(any(Trainee.class));
    }

    @Test
    @DisplayName("Negative - JSON parsing fails")
    void testInit_WhenJsonParseFails_ShouldNotCallServices() throws Exception {
        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<TraineeInitializeRequest>>>any()))
                .thenThrow(new IOException("Invalid JSON"));

        traineeInitializer.init();

        verify(userDao, never()).save(any());
        verify(traineeService, never()).createTrainee(any());
        verify(traineeDao, never()).save(any());
    }

    @Test
    @DisplayName("Negative - InputStream fails")
    void testInit_WhenInputStreamFails_ShouldNotCallServices() throws Exception {
        when(traineeDataFile.getInputStream())
                .thenThrow(new IOException("File not found"));

        traineeInitializer.init();

        verify(userDao, never()).save(any());
        verify(traineeService, never()).createTrainee(any());
        verify(traineeDao, never()).save(any());
    }

    @Test
    @DisplayName("Edge Case - Empty JSON list")
    void testInit_WhenJsonListEmpty_ShouldNotCallServices() throws Exception {
        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<TraineeInitializeRequest>>>any()))
                .thenReturn(List.of());

        traineeInitializer.init();

        verify(userDao, never()).save(any());
        verify(traineeService, never()).createTrainee(any());
        verify(traineeDao, never()).save(any());
    }
}
