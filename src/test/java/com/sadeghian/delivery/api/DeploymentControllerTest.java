package com.sadeghian.delivery.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeploymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnApplicationInfo() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application")
                        .value("spring-k8s-delivery"))
                .andExpect(jsonPath("$.version")
                        .value("dev"))
                .andExpect(jsonPath("$.status")
                        .value("running"));
    }
}