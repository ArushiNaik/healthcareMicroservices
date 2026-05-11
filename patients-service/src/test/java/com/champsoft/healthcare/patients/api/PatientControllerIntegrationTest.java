package com.champsoft.healthcare.patients.api;

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

// Integration test → tests the API layer with Spring Boot
// Uses MockMvc to send fake HTTP requests to the controller
// Uses the "testing" profile with H2 in-memory database
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class PatientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldTestPatientApiFullHappyPath() throws Exception {

        // ------------------- Step 1: Create patient -------------------
        // Insurance card must match ^[A-Z]{4}\d{8}$ (4 letters + 8 digits)
        // Patient must be adult (dateOfBirth in the past > 18 years)
        MvcResult createResult = mockMvc.perform(post("/api/patients")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Brown",
                                  "phoneNumber": "514-555-9001",
                                  "email": "alice.happy9001@example.com",
                                  "dateOfBirth": "1990-01-15",
                                  "healthCardNum": "ABCD10101010",
                                  "expiryDate": "2030-12-31",
                                  "streetNumber": 123,
                                  "streetName": "Main Street",
                                  "city": "Montreal",
                                  "postalCode": "H1A1A1",
                                  "Country": "Canada"
                                }
                                """))
                // API returns 200 OK
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.status").value("STABLE"))
                .andReturn();

        // Extract the generated patient ID
        String body = createResult.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        String patientId = json.get("id").asText();

        // ------------------- Step 2: Get patient by ID -------------------
        mockMvc.perform(get("/api/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId))
                .andExpect(jsonPath("$.firstName").value("Alice"));

        // ------------------- Step 3: List patients -------------------
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        // ------------------- Step 4: Eligibility (adult → true) -------------------
        mockMvc.perform(get("/api/patients/{id}/eligibility", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // ------------------- Step 5: Update insurance card -------------------
        mockMvc.perform(put("/api/patients/{id}/insuranceCard", patientId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "%s",
                                  "healthCardNum": "WXYZ20202020",
                                  "expiryDate": "2032-06-30"
                                }
                                """.formatted(patientId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId));

        // ------------------- Step 6: Update address -------------------
        mockMvc.perform(put("/api/patients/{id}/address", patientId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "%s",
                                  "streetNumber": 456,
                                  "streetName": "Elm Street",
                                  "city": "Quebec City",
                                  "postalCode": "G1A1A1",
                                  "Country": "Canada"
                                }
                                """.formatted(patientId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId));

        // ------------------- Step 7: Change status to CRITICAL -------------------
        mockMvc.perform(post("/api/patients/{id}/status/{status}", patientId, "CRITICAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CRITICAL"));

        // ------------------- Step 8: Delete patient -------------------
        // PatientController.delete returns void → Spring returns 200 OK with empty body
        mockMvc.perform(delete("/api/patients/{id}", patientId))
                .andExpect(status().isOk());
    }
}
