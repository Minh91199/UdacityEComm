package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.requests.CreateItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    private List<Item> testItemList;
    private Item testItem;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initializeTestData();
    }

    private void initializeTestData() {
        testItem = new Item(1L, "item1", "description1", BigDecimal.TEN);
        testItemList = new ArrayList<>();
        testItemList.add(testItem);
    }

    @Test
    public void getItems_ShouldReturnOkAndListOfItems() {
        when(itemRepository.findAll()).thenReturn(testItemList);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertResponseList(response, HttpStatus.OK, testItemList);
    }

    @Test
    public void getItemById_ShouldReturnOkAndItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertResponseItem(response, HttpStatus.OK, testItem);
    }

    @Test
    public void getItemsByName_ShouldReturnOkAndListOfItems() {
        when(itemRepository.findByName("item1")).thenReturn(testItemList);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("item1");

        assertResponseList(response, HttpStatus.OK, testItemList);
    }

    @Test
    public void createItem_ShouldReturnOkAndNewItem() {
        CreateItemRequest request = new CreateItemRequest("item1", BigDecimal.TEN, "description1");
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        ResponseEntity<?> response = itemController.createItem(request);

        assertResponseItem((ResponseEntity<Item>) response, HttpStatus.OK, testItem);
    }

    private void assertResponseList(ResponseEntity<List<Item>> response, HttpStatus expectedStatus, List<Item> expectedBody) {
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedStatus, response.getStatusCode(), "Status should match expected");
        assertEquals(expectedBody, response.getBody(), "Body should match expected");
    }

    private void assertResponseItem(ResponseEntity<Item> response, HttpStatus expectedStatus, Item expectedBody) {
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedStatus, response.getStatusCode(), "Status should match expected");
        assertEquals(expectedBody, response.getBody(), "Body should match expected");
    }
}
