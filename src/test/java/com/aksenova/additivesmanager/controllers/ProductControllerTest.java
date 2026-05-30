package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.entity.Product;
import com.aksenova.additivesmanager.exception.GlobalExceptionHandler;
import com.aksenova.additivesmanager.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock ProductService service;
    @InjectMocks ProductController controller;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getList_returns200() throws Exception {
        when(service.getAllProducts()).thenReturn(List.of());
        mvc.perform(get("/api/products/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void create_validDto_returns200() throws Exception {
        var created = new Product(); created.setId(5); created.setProductName("Е100");
        when(service.createProduct(any())).thenReturn(created);

        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productName\":\"Е100\",\"productTypeId\":1,\"statusId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Е100"));
    }

    @Test
    void create_missingName_returns400() throws Exception {
        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productTypeId\":1,\"statusId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(service.getProductById(99)).thenReturn(Optional.empty());
        mvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_callsService() throws Exception {
        mvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
        verify(service).deleteProduct(1);
    }
}
