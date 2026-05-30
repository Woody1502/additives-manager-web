package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.dto.ProductDto;
import com.aksenova.additivesmanager.entity.Product;
import com.aksenova.additivesmanager.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ProductControllerTest {

    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean ProductService service;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    void getList_authenticated_returns200() throws Exception {
        when(service.getAllProducts()).thenReturn(List.of());
        mvc.perform(get("/api/products/list"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void create_validDto_returns200() throws Exception {
        var dto = new ProductDto(); dto.setProductName("Е100"); dto.setProductTypeId(1); dto.setStatusId(1);
        var created = new Product(); created.setId(5); created.setProductName("Е100");
        when(service.createProduct(any())).thenReturn(created);

        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Е100"));
    }

    @Test
    @WithMockUser
    void create_missingName_returns400() throws Exception {
        var dto = new ProductDto(); dto.setProductTypeId(1); dto.setStatusId(1);
        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getById_notFound_returns404() throws Exception {
        when(service.getProductById(99)).thenReturn(Optional.empty());
        mvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getList_unauthenticated_returns401() throws Exception {
        mvc.perform(get("/api/products/list"))
                .andExpect(status().isUnauthorized());
    }
}
