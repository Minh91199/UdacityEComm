package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private User testUser;
    private CreateUserRequest testCreateUserRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initializeTestData();
    }

    private void initializeTestData() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encryptedPassword");

        testCreateUserRequest = new CreateUserRequest();
        testCreateUserRequest.setUsername("testuser");
        testCreateUserRequest.setPassword("password");
        testCreateUserRequest.setConfirmPassword("password");
    }

    @Test
    public void findById_ShouldReturnUserAndStatusOk() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.findById(testUser.getId());

        assertUserResponse(response, HttpStatus.OK, testUser);
    }

    @Test
    public void findByUserName_ShouldReturnUserAndStatusOk() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);

        ResponseEntity<User> response = userController.findByUserName(testUser.getUsername());

        assertUserResponse(response, HttpStatus.OK, testUser);
    }

    @Test
    public void createUser_ShouldReturnCreatedUserAndStatusOk() {
        when(userRepository.findByUsername(testCreateUserRequest.getUsername())).thenReturn(null);
        when(bCryptPasswordEncoder.encode(testCreateUserRequest.getPassword())).thenReturn("encryptedPassword");

        ResponseEntity<User> response = userController.createUser(testCreateUserRequest);

        User createdUser = response.getBody();
        assertNotNull(createdUser, "Created user should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        assertEquals(testCreateUserRequest.getUsername(), createdUser.getUsername(), "Username should match");
        assertEquals("encryptedPassword", createdUser.getPassword(), "Password should be encrypted");
        assertNotNull(createdUser.getCart(), "User should have a cart");
    }

    private void assertUserResponse(ResponseEntity<User> response, HttpStatus expectedStatus, User expectedUser) {
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedStatus, response.getStatusCode(), "Status should match expected");
        assertNotNull(response.getBody(), "User should not be null");
        assertEquals(expectedUser, response.getBody(), "User should match expected");
    }
}
