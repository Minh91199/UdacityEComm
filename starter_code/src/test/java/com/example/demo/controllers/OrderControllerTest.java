package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderController orderController;

    private User testUser;
    private List<Item> testItems;
    private UserOrder testOrder1, testOrder2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initializeTestData();
    }

    private void initializeTestData() {
        testUser = new User();
        testUser.setUsername("testuser");
        Cart cart = new Cart();

        Item item = new Item(1L, "Item 1", "Description 1", new BigDecimal("10.00"));
        testItems = new ArrayList<>();
        testItems.add(item);
        cart.setItems(testItems);
        testUser.setCart(cart);

        testOrder1 = new UserOrder();
        testOrder1.setUser(testUser);
        testOrder2 = new UserOrder();
        testOrder2.setUser(testUser);
    }

    @Test
    public void submitOrder_ShouldReturnOkAndSaveOrder() {
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        ResponseEntity<UserOrder> response = orderController.submit("testuser");

        verify(orderRepository, times(1)).save(any(UserOrder.class));
        assertResponse(response, HttpStatus.OK, testUser.getCart().getItems().size());
    }

    @Test
    public void submitOrder_WithInvalidUser_ShouldReturnNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("testuser");

        verify(orderRepository, never()).save(any(UserOrder.class));
        assertResponse(response, HttpStatus.NOT_FOUND, null);
    }

    @Test
    public void getOrdersForUser_ShouldReturnOkAndOrderList() {
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(orderRepository.findByUser(testUser)).thenReturn(Arrays.asList(testOrder1, testOrder2));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testuser");

        assertOrderListResponse(response, HttpStatus.OK, 2);
    }

    @Test
    public void getOrdersForInvalidUser_ShouldReturnNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testuser");

        assertOrderListResponse(response, HttpStatus.NOT_FOUND, 0);
    }

    private void assertResponse(ResponseEntity<UserOrder> response, HttpStatus expectedStatus, Integer expectedItemSize) {
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedStatus, response.getStatusCode(), "Status should match expected");
        if (expectedItemSize != null) {
            assertNotNull(response.getBody(), "Order should not be null");
            assertEquals(expectedItemSize, response.getBody().getItems().size(), "Order item size should match expected");
        } else {
            assertNull(response.getBody(), "Order should be null");
        }
    }

    private void assertOrderListResponse(ResponseEntity<List<UserOrder>> response, HttpStatus expectedStatus, int expectedSize) {
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedStatus, response.getStatusCode(), "Status should match expected");
        if (expectedStatus == HttpStatus.OK) {
            assertNotNull(response.getBody(), "Order list should not be null");
            assertEquals(expectedSize, response.getBody().size(), "Order list size should match expected");
        } else {
            assertNull(response.getBody(), "Order list should be null");
        }
    }
}
