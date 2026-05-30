package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.dto.ManufacturerDto;
import com.aksenova.additivesmanager.entity.Manufacturer;
import com.aksenova.additivesmanager.service.ManufacturerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ManufacturerController.class)
@Import({com.aksenova.additivesmanager.security.JwtAuthFilter.class,
         com.aksenova.additivesmanager.security.JwtUtil.class})
class ManufacturerControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean ManufacturerService service;

    @Test
    @WithMockUser
    void getAll_authenticated_returns200() throws Exception {
        when(service.getAllManufacturers()).thenReturn(List.of());

        mvc.perform(get("/api/manufacturers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void create_validDto_returns200() throws Exception {
        var dto = new ManufacturerDto();
        dto.setName("ООО Тест");
        dto.setCountry("Россия");

        var created = new Manufacturer();
        created.setId(1);
        created.setName("ООО Тест");

        when(service.createManufacturer(any())).thenReturn(created);

        mvc.perform(post("/api/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ООО Тест"));
    }

    @Test
    @WithMockUser
    void create_missingName_returns400() throws Exception {
        mvc.perform(post("/api/manufacturers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"country\":\"Россия\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getById_notFound_returns404() throws Exception {
        when(service.getManufacturerById(99)).thenReturn(Optional.empty());

        mvc.perform(get("/api/manufacturers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mvc.perform(get("/api/manufacturers"))
                .andExpect(status().isUnauthorized());
    }
}
