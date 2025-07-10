package com.epam.springcore.service.impl;

import com.epam.springcore.dao.UserDao;
import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.User;
import com.epam.springcore.service.ITraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private ITraineeService traineeService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName(" Positive Scenario - Create and Save User with Trainee Data")
    void testUserCreationWithTraineeData() {
        // arrange
        List<Trainee> mockTrainees = List.of(new Trainee(), new Trainee());
        when(traineeService.getAllTrainees()).thenReturn(mockTrainees);

        User user = new User("Ayşe", "Demir", traineeService.getAllTrainees());
        doNothing().when(userDao).save(user);
        userService.save(user);

        // assert
        assertNotNull(user.getUsername());
        assertNotNull(user.getPassword());
        assertTrue(user.isActive());
        verify(userDao, times(1)).save(user);
    }

    @Test
    @DisplayName(" Negative Scenario - Null FirstName Should Throw Exception")
    void testUserCreationWithTraineeData_NullFirstName_ShouldFail() {
        // arrange
        List<Trainee> mockTrainees = List.of(new Trainee(), new Trainee());
        when(traineeService.getAllTrainees()).thenReturn(mockTrainees);

        assertThrows(IllegalArgumentException.class, () -> {
            User user = new User(null, "Demir", traineeService.getAllTrainees());
            userService.save(user);
        });

        verify(userDao, never()).save(any());
    }
    @Test
    @DisplayName(" Positive Scenario - Find User With User Id")
    void testUserFindById() {
        User mockUser = new User("Ali", "Yılmaz", traineeService.getAllTrainees());
        when(userDao.findById("1")).thenReturn(mockUser);
        userService.findById("1");
        verify(userDao, times(1)).findById("1");
    }
    @Test
    @DisplayName("Negative Scenario - Throws IllegalArgumentException When User Id Is Null")
    void testUserFindById_NullUserId_ShouldFail() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.findById(null);
        });
        verify(userDao, never()).findById(any());
    }
    @Test
    @DisplayName("Positive Scenario - Find All Users")
    void testUserFindAll() {
        List<User> userList = List.of(
                new User("Ali", "Yılmaz", traineeService.getAllTrainees()),
                new User("Ayşe", "Demir", traineeService.getAllTrainees())
        );
        when(userDao.findAll()).thenReturn(userList);

        Collection<User> result = userService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(userList);

        verify(userDao, times(1)).findAll();
    }
    @Test
    @DisplayName("Positive Scenario - Delete User By User Id")
    void testUserDeleteById() {
        User mockUser = new User("Ali", "Yılmaz", traineeService.getAllTrainees());
        when(userDao.findById(mockUser.getId())).thenReturn(mockUser);
        userService.delete(mockUser.getId());
        verify(userDao, times(1)).delete(mockUser.getId());
    }




}
