package com.champsoft.healthcare.appointments.api;

import com.champsoft.healthcare.appointments.application.exceptions.CrossContextValidationException;
import com.champsoft.healthcare.appointments.application.port.out.BillingPort;
import com.champsoft.healthcare.appointments.application.port.out.DoctorEligibilityPort;
import com.champsoft.healthcare.appointments.application.port.out.PatientEligibilityPort;
import com.champsoft.healthcare.appointments.domain.exception.AppointmentNotFoundException;
import com.champsoft.healthcare.appointments.domain.exception.TimeSlotConflictException;
import com.champsoft.healthcare.appointments.web.ApiErrorResponse;
import com.champsoft.healthcare.appointments.web.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Negative integration test → tests invalid API scenarios
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class AppointmentControllerNegativeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorEligibilityPort doctorEligibilityPort;

    @MockitoBean
    private PatientEligibilityPort patientEligibilityPort;

    @MockitoBean
    private BillingPort billingPort;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final String FUTURE_TIME =
            LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    @Test
    void shouldReturnConflictWhenDoctorDoesNotExist() throws Exception {

        // ------------------- Arrange -------------------
        // doctorPort.exists returns false →
        // orchestrator throws CrossContextValidationException →
        // AppointmentExceptionHandler maps to 409 Conflict
        when(doctorEligibilityPort.exists(anyString())).thenReturn(false);

        // ------------------- Act + Assert -------------------
        mockMvc.perform(post("/api/appointments")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "doctorId":  "bad-doctor",
                                  "patientId": "patient-1",
                                  "billingId": "billing-1",
                                  "time": "%s"
                                }
                                """.formatted(FUTURE_TIME)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnConflictWhenPatientDoesNotExist() throws Exception {

        // ------------------- Arrange -------------------
        when(doctorEligibilityPort.exists(anyString())).thenReturn(true);
        when(patientEligibilityPort.exists(anyString())).thenReturn(false);

        // ------------------- Act + Assert -------------------
        mockMvc.perform(post("/api/appointments")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "doctorId":  "doctor-1",
                                  "patientId": "bad-patient",
                                  "billingId": "billing-1",
                                  "time": "%s"
                                }
                                """.formatted(FUTURE_TIME)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnConflictWhenDoctorPortThrowsCrossContextException() throws Exception {

        // ------------------- Arrange -------------------
        // ACL adapter can throw CrossContextValidationException directly
        when(doctorEligibilityPort.exists(anyString()))
                .thenThrow(new CrossContextValidationException("Doctor service unavailable"));

        // ------------------- Act + Assert -------------------
        mockMvc.perform(post("/api/appointments")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "doctorId":  "doctor-down",
                                  "patientId": "patient-1",
                                  "billingId": "billing-1",
                                  "time": "%s"
                                }
                                """.formatted(FUTURE_TIME)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundWhenAppointmentDoesNotExist() throws Exception {

        // ------------------- Act + Assert -------------------
        mockMvc.perform(get("/api/appointments/{id}", "non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingAppointment() throws Exception {

        // ------------------- Act + Assert -------------------
        mockMvc.perform(delete("/api/appointments/{id}", "non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenReschedulingMissingAppointment() throws Exception {

        // ------------------- Act + Assert -------------------
        mockMvc.perform(put("/api/appointments/{id}/reschedule", "non-existent-id")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "newTime": "%s"
                                }
                                """.formatted(FUTURE_TIME)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenCreatingAppointmentWithMalformedJson() throws Exception {

        mockMvc.perform(post("/api/appointments")
                        .contentType(APPLICATION_JSON)
                        .content("""
                            {
                              "doctorId": "d1"
                            """))
                .andExpect(res -> org.assertj.core.api.Assertions.assertThat(
                        res.getResponse().getStatus()).isGreaterThanOrEqualTo(400));
    }

    // -----------------------------------------------------------------------
    // Direct exception-handler tests (same pattern as VRMS)
    // -----------------------------------------------------------------------

    @Test
    void shouldDirectlyTestAppointmentNotFoundExceptionHandler() {

        // ------------------- Arrange -------------------
        AppointmentExceptionHandler handler = new AppointmentExceptionHandler();
        HttpServletRequest request = request("/api/appointments/missing");

        // ------------------- Act -------------------
        // AppointmentExceptionHandler.handleNotFound maps domain.exception.AppointmentNotFoundException → 404
        ResponseEntity<ApiErrorResponse> response =
                handler.handleNotFound(
                        new AppointmentNotFoundException("Appointment not found: missing"),
                        request);

        // ------------------- Assert -------------------
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().message()).isEqualTo("Appointment not found: missing");
        assertThat(response.getBody().path()).isEqualTo("/api/appointments/missing");
    }

    @Test
    void shouldDirectlyTestCrossContextValidationExceptionHandler() {

        // ------------------- Arrange -------------------
        AppointmentExceptionHandler handler = new AppointmentExceptionHandler();
        HttpServletRequest request = request("/api/appointments");

        // ------------------- Act -------------------
        // CrossContextValidationException → 409 Conflict
        ResponseEntity<ApiErrorResponse> response =
                handler.unprocessable(
                        new CrossContextValidationException("Doctor not found"),
                        request);

        // ------------------- Assert -------------------
        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().error()).isEqualTo("Conflict");
        assertThat(response.getBody().message()).isEqualTo("Doctor not found");
    }

    @Test
    void shouldDirectlyTestTimeSlotConflictExceptionHandler() {

        // ------------------- Arrange -------------------
        AppointmentExceptionHandler handler = new AppointmentExceptionHandler();
        HttpServletRequest request = request("/api/appointments");

        // ------------------- Act -------------------
        // TimeSlotConflictException → 409 Conflict
        ResponseEntity<ApiErrorResponse> response =
                handler.handleConflict(
                        new TimeSlotConflictException("Time slot already taken"),
                        request);

        // ------------------- Assert -------------------
        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().error()).isEqualTo("Conflict");
        assertThat(response.getBody().message()).isEqualTo("Time slot already taken");
    }

    @Test
    void shouldDirectlyTestGlobalExceptionHandlerWithMessage() {

        // ------------------- Arrange -------------------
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = request("/api/appointments/test");

        // ------------------- Act -------------------
        ResponseEntity<ApiErrorResponse> response =
                handler.handleAny(new RuntimeException("Something went wrong"), request);

        // ------------------- Assert -------------------
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().message()).isEqualTo("Something went wrong");
    }

    @Test
    void shouldDirectlyTestGlobalExceptionHandlerWithNullMessage() {

        // ------------------- Arrange -------------------
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = request("/api/appointments/test");

        // ------------------- Act -------------------
        ResponseEntity<ApiErrorResponse> response =
                handler.handleAny(new RuntimeException(), request);

        // ------------------- Assert -------------------
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Unexpected error");
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(path);
        return req;
    }
}
