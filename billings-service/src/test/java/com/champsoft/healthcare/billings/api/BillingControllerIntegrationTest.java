package com.champsoft.healthcare.billings.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Integration test → tests the full API layer with Spring Boot
// Uses MockMvc to send fake HTTP requests to the controller
// Uses the "testing" profile with H2 in-memory database
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class BillingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Test
    void shouldTestBillingApiFullHappyPath() throws Exception {

        // ------------------- Step 1: Create billing -------------------
        // dueDate must be in the future
        // amount must be positive
        // method must be one of: CASH, CREDIT_CARD, INSURANCE
        MvcResult createResult = mockMvc.perform(post("/api/billings")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Consultation",
                                  "amount": 150.00,
                                  "dueDate": "2030-12-31",
                                  "method": "CASH"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Consultation"))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.paymentMethod").value("CASH"))
                // Business rule: new billing starts as PENDING.
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        // Extract the generated billing ID.
        String body = createResult.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        String billingId = json.get("id").asText();

        // ------------------- Step 2: Get billing by ID -------------------
        mockMvc.perform(get("/api/billings/{id}", billingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(billingId))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // ------------------- Step 3: List billings -------------------
        mockMvc.perform(get("/api/billings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        // ------------------- Step 4: Update billing item -------------------
        mockMvc.perform(put("/api/billings/{id}", billingId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Lab Test",
                                  "amount": 200.00
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Lab Test"))
                .andExpect(jsonPath("$.amount").value(200.00));

        // ------------------- Step 5: Mark as PAID -------------------
        mockMvc.perform(post("/api/billings/{id}/paid", billingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        // ------------------- Step 6: Eligibility check (PAID → true) -------------------
        mockMvc.perform(get("/api/billings/{id}/eligibility", billingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // ------------------- Step 7: Refund -------------------
        mockMvc.perform(post("/api/billings/{id}/refund", billingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        // ------------------- Step 8: Create another billing to delete -------------------
        MvcResult secondResult = mockMvc.perform(post("/api/billings")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "X-Ray",
                                  "amount": 300.00,
                                  "dueDate": "2030-12-31",
                                  "method": "CREDIT_CARD"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String secondId = objectMapper.readTree(
                secondResult.getResponse().getContentAsString()).get("id").asText();

        // ------------------- Step 9: Delete billing -------------------
        // BillingController.delete → ResponseEntity<Void> → 204 No Content
        mockMvc.perform(delete("/api/billings/{id}", secondId))
                .andExpect(status().isNoContent());
    }
}
