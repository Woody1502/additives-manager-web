package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.dto.ProductTypeDto;
import com.aksenova.additivesmanager.entity.ProductType;
import com.aksenova.additivesmanager.service.ProductTypeService;
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
class ProductTypeControllerTest {

    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean ProductTypeService service;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    void getAll_authenticated_returns200() throws Exception {
        when(service.getAllProductTypes()).thenReturn(List.of());
        mvc.perform(get("/api/product-types"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_adminRole_returns200() throws Exception {
        var dto = new ProductTypeDto(); dto.setTypeName("БАД");
        var created = new ProductType(); created.setId(1); created.setTypeName("БАД");
        when(service.createProductType(any())).thenReturn(created);

        mvc.perform(post("/api/product-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeName").value("БАД"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_blankName_returns400() throws Exception {
        mvc.perform(post("/api/product-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"typeName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_userRole_returns403() throws Exception {
        var dto = new ProductTypeDto(); dto.setTypeName("БАД");
        mvc.perform(post("/api/product-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_adminRole_returns204() throws Exception {
        mvc.perform(delete("/api/product-types/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_userRole_returns403() throws Exception {
        mvc.perform(delete("/api/product-types/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mvc.perform(get("/api/product-types"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getById_notFound_returns404() throws Exception {
        when(service.getProductTypeById(99)).thenReturn(Optional.empty());
        mvc.perform(get("/api/product-types/99"))
                .andExpect(status().isNotFound());
    }
}
