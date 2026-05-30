package com.aksenova.additivesmanager.controllers;

import com.aksenova.additivesmanager.dto.ProductStatusDto;
import com.aksenova.additivesmanager.entity.ProductStatus;
import com.aksenova.additivesmanager.service.ProductStatusService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductStatusController.class)
@Import({com.aksenova.additivesmanager.security.JwtAuthFilter.class,
         com.aksenova.additivesmanager.security.JwtUtil.class})
class ProductStatusControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean ProductStatusService service;

    @Test
    @WithMockUser
    void getAll_authenticated_returns200() throws Exception {
        when(service.getAllProductStatuses()).thenReturn(List.of());

        mvc.perform(get("/api/product-statuses"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_adminRole_returns200() throws Exception {
        var dto = new ProductStatusDto();
        dto.setStatusName("действует");

        var created = new ProductStatus();
        created.setId(1);
        created.setStatusName("действует");

        when(service.createProductStatus(any())).thenReturn(created);

        mvc.perform(post("/api/product-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusName").value("действует"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_blankName_returns400() throws Exception {
        mvc.perform(post("/api/product-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statusName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_userRole_returns403() throws Exception {
        var dto = new ProductStatusDto();
        dto.setStatusName("действует");

        mvc.perform(post("/api/product-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_userRole_returns403() throws Exception {
        mvc.perform(delete("/api/product-statuses/1"))
                .andExpect(status().isForbidden());
    }
}
