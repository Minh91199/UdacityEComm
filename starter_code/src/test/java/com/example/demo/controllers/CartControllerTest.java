package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartController cartController;

    private ModifyCartRequest modifyCartRequest;
    private User testUser;
    private Cart testCart;
    private Item testItem;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initializeTestData();
    }

    private void initializeTestData() {
        testUser = new User();
        testUser.setUsername("test_user");
        testUser.setPassword("test_password");

        testCart = new Cart();
        testUser.setCart(testCart);

        testItem = new Item(1L, "Test Item", "Test Item Description", BigDecimal.valueOf(10.0));

        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(testUser.getUsername());
        modifyCartRequest.setItemId(testItem.getId());
        modifyCartRequest.setQuantity(1);
    }

    private void mockRepositoryBehavior() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
    }

    @Test
    public void addToCart_ShouldReturnOkStatus() {
        mockRepositoryBehavior();
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        assertResponse(responseEntity, HttpStatus.OK, testCart);
    }

    @Test
    public void removeFromCart_ShouldReturnOkStatus() {
        mockRepositoryBehavior();
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);
        assertResponse(responseEntity, HttpStatus.OK, testCart);
    }

    private void assertResponse(ResponseEntity<Cart> responseEntity, HttpStatus expectedStatus, Cart expectedBody) {
        assertNotNull(responseEntity, "Response should not be null");
        assertEquals(expectedStatus, responseEntity.getStatusCode(), "Status should match expected");
        assertEquals(expectedBody, responseEntity.getBody(), "Body should match expected");
    }
}
