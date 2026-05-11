package com.champsoft.healthcare.appointments.api;

import com.champsoft.healthcare.appointments.application.port.out.BillingPort;
import com.champsoft.healthcare.appointments.application.port.out.DoctorEligibilityPort;
import com.champsoft.healthcare.appointments.application.port.out.PatientEligibilityPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Integration test → tests the full API layer with Spring Boot
// Uses MockMvc to send fake HTTP requests to the controller
// Uses @MockitoBean to replace the 3 REST adapters (no real external services)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class AppointmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Replace the 3 external REST adapters with mocks.
    @MockitoBean
    private DoctorEligibilityPort doctorEligibilityPort;

    @MockitoBean
    private PatientEligibilityPort patientEligibilityPort;

    @MockitoBean
    private BillingPort billingPort;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    // Future time for creating valid appointments
    private static final String FUTURE_TIME =
            LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    private static final String NEW_TIME =
            LocalDateTime.now().plusDays(60).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    @Test
    void shouldTestAppointmentApiFullHappyPath() throws Exception {

        // ------------------- Setup: mock external services as available -------------------
        when(doctorEligibilityPort.exists(anyString())).thenReturn(true);
        when(patientEligibilityPort.exists(anyString())).thenReturn(true);
        when(billingPort.exists(anyString())).thenReturn(true);

        // ------------------- Step 1: Create appointment -------------------
        // Controller returns EntityModel<AppointmentResponse> (HATEOAS)
        // Fields are at the root level alongside "_links"
        MvcResult createResult = mockMvc.perform(post("/api/appointments")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "doctorId":  "doctor-abc",
                                  "patientId": "patient-abc",
                                  "billingId": "billing-abc",
                                  "time": "%s"
                                }
                                """.formatted(FUTURE_TIME)))
                // Controller returns 200 OK (no @ResponseStatus, default is 200)
                .andExpect(status().isOk())
                // HATEOAS EntityModel wraps fields at the root
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.doctorId").value("doctor-abc"))
                .andExpect(jsonPath("$.patientId").value("patient-abc"))
                .andExpect(jsonPath("$.billingId").value("billing-abc"))
                // Business rule: new appointment starts SCHEDULED.
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                // HATEOAS: self link must be present.
                .andExpect(jsonPath("$._links.self.href").exists())
                .andReturn();

        // Extract the generated appointment ID.
        String body = createResult.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        String apptId = json.get("id").asText();

        // ------------------- Step 2: Get appointment by ID -------------------
        mockMvc.perform(get("/api/appointments/{id}", apptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(apptId))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$._links.self.href").exists());

        // ------------------- Step 3: List appointments -------------------
        // Controller returns CollectionModel → _embedded contains the list
        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists());

        // ------------------- Step 4: Reschedule (PUT /{id}/reschedule) -------------------
        mockMvc.perform(put("/api/appointments/{id}/reschedule", apptId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "newTime": "%s"
                                }
                                """.formatted(NEW_TIME)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(apptId))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        // ------------------- Step 5: Delete (DELETE /{id}) -------------------
        // Controller: ResponseEntity.noContent().build() → 204 No Content
        mockMvc.perform(delete("/api/appointments/{id}", apptId))
                .andExpect(status().isNoContent());
    }
}
