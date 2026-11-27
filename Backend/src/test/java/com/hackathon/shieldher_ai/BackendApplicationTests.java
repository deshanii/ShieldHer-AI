package com.hackathon.shieldher_ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void listsSeededScenariosAndCountrySpecificSupport() throws Exception {
        mvc.perform(get("/api/v1/scenarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
        mvc.perform(get("/api/v1/helplines").param("country", "PK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[?(@.contactInfo == '1799')].sourceUrl")
                        .value(org.hamcrest.Matchers.hasItem("https://complaint.nccia.gov.pk/")));
    }

    @Test
    void returnsSinhalaSafetyGuidanceWithoutInventingRisk() throws Exception {
        mvc.perform(post("/api/v1/threat/analyze").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Synthetic sample message\",\"language\":\"si\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("UNAVAILABLE"))
                .andExpect(jsonPath("$.riskScore").doesNotExist())
                .andExpect(jsonPath("$.summary").value(org.hamcrest.Matchers.containsString("විශ්ලේෂණය")))
                .andExpect(jsonPath("$.actionSteps.length()").value(2));
    }

    @Test
    void validatesMessageAndEmailBeforeCallingProviders() throws Exception {
        mvc.perform(post("/api/v1/threat/analyze").contentType(MediaType.APPLICATION_JSON).content("{\"content\":\" \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/check-ins").contentType(MediaType.APPLICATION_JSON).content("{\"recipient\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
    }
}
