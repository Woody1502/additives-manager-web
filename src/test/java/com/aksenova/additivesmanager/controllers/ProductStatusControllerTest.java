package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.entity.ProductStatus;
import com.aksenova.additivesmanager.exception.GlobalExceptionHandler;
import com.aksenova.additivesmanager.service.ProductStatusService;
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
class ProductStatusControllerTest {

    @Mock ProductStatusService service;
    @InjectMocks ProductStatusController controller;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_returns200() throws Exception {
        when(service.getAllProductStatuses()).thenReturn(List.of());
        mvc.perform(get("/api/product-statuses"))
                .andExpect(status().isOk());
    }

    @Test
    void create_validDto_returns200() throws Exception {
        var created = new ProductStatus(); created.setId(1); created.setStatusName("действует");
        when(service.createProductStatus(any())).thenReturn(created);

        mvc.perform(post("/api/product-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusName\":\"действует\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusName").value("действует"));
    }

    @Test
    void create_blankName_returns400() throws Exception {
        mvc.perform(post("/api/product-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(service.getProductStatusById(99)).thenReturn(Optional.empty());
        mvc.perform(get("/api/product-statuses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_callsService() throws Exception {
        mvc.perform(delete("/api/product-statuses/1"))
                .andExpect(status().isNoContent());
        verify(service).deleteProductStatus(1);
    }
}
