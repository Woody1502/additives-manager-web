package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.entity.Manufacturer;
import com.aksenova.additivesmanager.exception.GlobalExceptionHandler;
import com.aksenova.additivesmanager.service.ManufacturerService;
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
class ManufacturerControllerTest {

    @Mock ManufacturerService service;
    @InjectMocks ManufacturerController controller;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAll_returns200() throws Exception {
        when(service.getAllManufacturers()).thenReturn(List.of());
        mvc.perform(get("/api/manufacturers"))
                .andExpect(status().isOk());
    }

    @Test
    void create_validDto_returns200() throws Exception {
        var created = new Manufacturer(); created.setId(1); created.setName("ООО Тест");
        when(service.createManufacturer(any())).thenReturn(created);

        mvc.perform(post("/api/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"ООО Тест\",\"country\":\"Россия\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ООО Тест"));
    }

    @Test
    void create_missingName_returns400() throws Exception {
        mvc.perform(post("/api/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"country\":\"Россия\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(service.getManufacturerById(99)).thenReturn(Optional.empty());
        mvc.perform(get("/api/manufacturers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_callsService() throws Exception {
        mvc.perform(delete("/api/manufacturers/1"))
                .andExpect(status().isNoContent());
        verify(service).deleteManufacturer(1);
    }
}
