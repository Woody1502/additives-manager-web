package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.dto.ProductTypeDto;
import com.aksenova.additivesmanager.entity.ProductType;
import com.aksenova.additivesmanager.exception.GlobalExceptionHandler;
import com.aksenova.additivesmanager.service.ProductTypeService;
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
class ProductTypeControllerTest {

    @Mock ProductTypeService service;
    @InjectMocks ProductTypeController controller;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_returns200() throws Exception {
        when(service.getAllProductTypes()).thenReturn(List.of());
        mvc.perform(get("/api/product-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void create_validDto_returns200() throws Exception {
        var created = new ProductType(); created.setId(1); created.setTypeName("БАД");
        when(service.createProductType(any())).thenReturn(created);

        mvc.perform(post("/api/product-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"typeName\":\"БАД\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.typeName").value("БАД"));
    }

    @Test
    void create_blankName_returns400() throws Exception {
        mvc.perform(post("/api/product-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"typeName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(service.getProductTypeById(99)).thenReturn(Optional.empty());
        mvc.perform(get("/api/product-types/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_callsService() throws Exception {
        mvc.perform(delete("/api/product-types/1"))
                .andExpect(status().isNoContent());
        verify(service).deleteProductType(1);
    }
}
