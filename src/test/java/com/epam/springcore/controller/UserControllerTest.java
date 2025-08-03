package com.epam.springcore.controller;

import com.epam.springcore.exception.InvalidCredentialsException;
import com.epam.springcore.request.user.ChangePasswordRequest;
import com.epam.springcore.request.user.LoginRequest;
import com.epam.springcore.service.IUserService;
import com.epam.springcore.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UserController Unit Tests")
class UserControllerTest {

    private IUserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserServiceImpl.class);
        userController = new UserController(userService);
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void loginSuccess() {
            LoginRequest request = new LoginRequest("john.doe", "pass123");

            assertDoesNotThrow(() -> userController.login(request));
            verify(userService).login(request);
        }

        @Test
        @DisplayName("Should fail login with invalid username")
        void loginInvalidUsername() {
            LoginRequest request = new LoginRequest("invalid.user", "pass123");

            doThrow(new InvalidCredentialsException("Invalid username or password"))
                    .when(userService).login(request);

            InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                    () -> userController.login(request));

            assertEquals("Invalid username or password", ex.getMessage());
            verify(userService).login(request);
        }

        @Test
        @DisplayName("Should fail login with wrong password")
        void loginWrongPassword() {
            LoginRequest request = new LoginRequest("john.doe", "wrongpass");

            doThrow(new InvalidCredentialsException("Invalid password"))
                    .when(userService).login(request);

            InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                    () -> userController.login(request));

            assertEquals("Invalid password", ex.getMessage());
            verify(userService).login(request);
        }

        @Test
        @DisplayName("Should throw error for null fields in request")
        void loginNullFields() {
            LoginRequest request = new LoginRequest(null, null);

            doThrow(new InvalidCredentialsException("Invalid username or password"))
                    .when(userService).login(request);

            InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                    () -> userController.login(request));

            assertEquals("Invalid username or password", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Change Password Tests")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should change password successfully")
        void changePasswordSuccess() {
            ChangePasswordRequest request = new ChangePasswordRequest("john.doe", "old123", "new456");

            assertDoesNotThrow(() -> userController.changePassword(request));
            verify(userService).changePassword(request);
        }

        @Test
        @DisplayName("Should fail with invalid username")
        void changePasswordInvalidUsername() {
            ChangePasswordRequest request = new ChangePasswordRequest("invalid.user", "old123", "new456");

            doThrow(new InvalidCredentialsException("Invalid username or password"))
                    .when(userService).changePassword(request);

            InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                    () -> userController.changePassword(request));

            assertEquals("Invalid username or password", ex.getMessage());
            verify(userService).changePassword(request);
        }

        @Test
        @DisplayName("Should fail with wrong old password")
        void changePasswordWrongOldPassword() {
            ChangePasswordRequest request = new ChangePasswordRequest("john.doe", "wrongOld", "new456");

            doThrow(new InvalidCredentialsException("Old password does not match"))
                    .when(userService).changePassword(request);

            InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                    () -> userController.changePassword(request));

            assertEquals("Old password does not match", ex.getMessage());
            verify(userService).changePassword(request);
        }

        @Test
        @DisplayName("Should fail with empty or null fields")
        void changePasswordInvalidFields() {
            ChangePasswordRequest request = new ChangePasswordRequest("", "", "");

            doThrow(new InvalidCredentialsException("Invalid username or password"))
                    .when(userService).changePassword(request);

            InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                    () -> userController.changePassword(request));

            assertEquals("Invalid username or password", ex.getMessage());
        }
    }
}
