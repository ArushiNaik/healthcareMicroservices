package com.champsoft.healthcare.doctors.api;

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
class DoctorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Test
    void shouldTestDoctorApiFullHappyPath() throws Exception {

        // ------------------- Step 1: Create doctor -------------------
        // licenseExpiryDate must be in the future
        MvcResult createResult = mockMvc.perform(post("/api/doctors")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Brown",
                                  "speciality": "Cardiology",
                                  "licenseExpiryDate": "2030-12-31"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Brown"))
                .andExpect(jsonPath("$.speciality").value("Cardiology"))
                // Business rule: new doctor is active by default.
                .andExpect(jsonPath("$.active").value(true))
                .andReturn();

        // Extract the generated doctor ID.
        String body = createResult.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        String doctorId = json.get("id").asText();

        // ------------------- Step 2: Get doctor by ID -------------------
        mockMvc.perform(get("/api/doctors/{id}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctorId))
                .andExpect(jsonPath("$.active").value(true));

        // ------------------- Step 3: List doctors -------------------
        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        // ------------------- Step 4: Eligibility (active + valid license → true) -------------------
        mockMvc.perform(get("/api/doctors/{id}/eligibility", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // ------------------- Step 5: Update doctor info (PUT /{id}/info) -------------------
        mockMvc.perform(put("/api/doctors/{id}/info", doctorId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Bob",
                                  "lastName": "Martin",
                                  "speciality": "Neurology"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.speciality").value("Neurology"));

        // ------------------- Step 6: Update license (PUT /{id}/license) -------------------
        mockMvc.perform(put("/api/doctors/{id}/license", doctorId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "licenseExpiryDate": "2035-01-01"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctorId));

        // ------------------- Step 7: Deactivate doctor (PUT /{id}/deactivate) -------------------
        mockMvc.perform(put("/api/doctors/{id}/deactivate", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        // ------------------- Step 8: Activate doctor (PUT /{id}/activate) -------------------
        mockMvc.perform(put("/api/doctors/{id}/activate", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        // ------------------- Step 9: Delete doctor -------------------
        // DoctorController.delete returns ResponseEntity.ok("Doctor deleted")
        mockMvc.perform(delete("/api/doctors/{id}", doctorId))
                .andExpect(status().isOk());
    }
}
