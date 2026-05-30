package com.aksenova.additivesmanager.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SecurityTest {

    @Autowired
    ApplicationContext applicationContext;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup((WebApplicationContext) applicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void unauthenticated_returns401() throws Exception {
        mvc.perform(get("/api/products/list"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void user_canReadProducts() throws Exception {
        mvc.perform(get("/api/products/list"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void user_cannotCreateProductType_returns403() throws Exception {
        mvc.perform(post("/api/product-types")
                        .contentType("application/json")
                        .content("{\"typeName\":\"Test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void user_cannotDeleteProductStatus_returns403() throws Exception {
        mvc.perform(delete("/api/product-statuses/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_canReadProducts() throws Exception {
        mvc.perform(get("/api/products/list"))
                .andExpect(status().isOk());
    }
}
