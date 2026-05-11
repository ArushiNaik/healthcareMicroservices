package com.champsoft.healthcare.patients.api;

import com.champsoft.healthcare.patients.application.exception.DuplicatePatientException;
import com.champsoft.healthcare.patients.application.exception.PatientNotFoundException;
import com.champsoft.healthcare.patients.domain.exception.ExpiredHealthInsuranceCardException;
import com.champsoft.healthcare.patients.domain.exception.InvalidInsuranceCardNumber;
import com.champsoft.healthcare.patients.web.ApiErrorResponse;
import com.champsoft.healthcare.patients.web.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Negative integration test → tests invalid API scenarios
// These tests make sure the API returns correct HTTP error codes
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class PatientControllerNegativeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReturnBadRequestWhenInsuranceCardFormatIsInvalid() throws Exception {

        // ------------------- Act + Assert -------------------
        // Business rule: card must match ^[A-Z]{4}\d{8}$ → "INVALID123" rejected
        mockMvc.perform(post("/api/patients")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Bob",
                                  "lastName": "Invalid",
                                  "phoneNumber": "514-555-9100",
                                  "email": "bob9100@example.com",
                                  "dateOfBirth": "1990-01-15",
                                  "healthCardNum": "INVALID123",
                                  "expiryDate": "2030-12-31",
                                  "streetNumber": 1,
                                  "streetName": "Main St",
                                  "city": "Montreal",
                                  "postalCode": "H1A1A1",
                                  "Country": "Canada"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenInsuranceCardIsExpired() throws Exception {

        // ------------------- Act + Assert -------------------
        // Business rule: expired insurance card is rejected.
        mockMvc.perform(post("/api/patients")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Carol",
                                  "lastName": "Expired",
                                  "phoneNumber": "514-555-9200",
                                  "email": "carol9200@example.com",
                                  "dateOfBirth": "1990-01-15",
                                  "healthCardNum": "ABCD11112222",
                                  "expiryDate": "2020-01-01",
                                  "streetNumber": 1,
                                  "streetName": "Main St",
                                  "city": "Montreal",
                                  "postalCode": "H1A1A1",
                                  "Country": "Canada"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenPatientIsMinor() throws Exception {

        // ------------------- Act + Assert -------------------
        // Business rule: patient must be 18+ to register.
        mockMvc.perform(post("/api/patients")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Dave",
                                  "lastName": "Minor",
                                  "phoneNumber": "514-555-9300",
                                  "email": "dave9300@example.com",
                                  "dateOfBirth": "2015-06-01",
                                  "healthCardNum": "ABCD33334444",
                                  "expiryDate": "2030-12-31",
                                  "streetNumber": 1,
                                  "streetName": "Main St",
                                  "city": "Montreal",
                                  "postalCode": "H1A1A1",
                                  "Country": "Canada"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenCreatingDuplicateInsuranceCard() throws Exception {

        // ------------------- Arrange -------------------
        String firstRequest = """
                {
                  "firstName": "Eve",
                  "lastName": "Dup",
                  "phoneNumber": "514-555-9401",
                  "email": "eve9401@example.com",
                  "dateOfBirth": "1990-01-15",
                  "healthCardNum": "ABCD55556666",
                  "expiryDate": "2030-12-31",
                  "streetNumber": 1,
                  "streetName": "Main St",
                  "city": "Montreal",
                  "postalCode": "H1A1A1",
                  "Country": "Canada"
                }
                """;
        mockMvc.perform(post("/api/patients")
                        .contentType(APPLICATION_JSON).content(firstRequest))
                .andExpect(status().isOk());

        // ------------------- Act + Assert -------------------
        // Second patient with same card → 409 Conflict
        String secondRequest = """
                {
                  "firstName": "Eve2",
                  "lastName": "Dup",
                  "phoneNumber": "514-555-9402",
                  "email": "eve9402@example.com",
                  "dateOfBirth": "1990-01-15",
                  "healthCardNum": "ABCD55556666",
                  "expiryDate": "2030-12-31",
                  "streetNumber": 2,
                  "streetName": "Elm St",
                  "city": "Montreal",
                  "postalCode": "H1A1A1",
                  "Country": "Canada"
                }
                """;
        mockMvc.perform(post("/api/patients")
                        .contentType(APPLICATION_JSON).content(secondRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundWhenPatientDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/patients/{id}", "missing-patient-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingPatientCard() throws Exception {
        mockMvc.perform(put("/api/patients/{id}/insuranceCard", "missing-patient-id")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "missing-patient-id",
                                  "healthCardNum": "WXYZ99998888",
                                  "expiryDate": "2030-12-31"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingPatient() throws Exception {
        mockMvc.perform(delete("/api/patients/{id}", "missing-patient-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnErrorWhenCreatingPatientWithMalformedJson() throws Exception {
        // Malformed JSON → Spring throws HttpMessageNotReadableException.
        // GlobalExceptionHandler catches all Exception → 500,
        // but Spring MVC default handling may return 400.
        // Both are acceptable error responses.
        mockMvc.perform(post("/api/patients")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Frank"
                                """))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(
                        result.getResponse().getStatus()).isGreaterThanOrEqualTo(400));
    }

    // -----------------------------------------------------------------------
    // Direct exception-handler tests (same pattern as VRMS)
    // These tests improve branch coverage for PatientExceptionHandler.
    // -----------------------------------------------------------------------

    @Test
    void shouldDirectlyTestPatientNotFoundExceptionHandler() {

        // ------------------- Arrange -------------------
        PatientExceptionHandler handler = new PatientExceptionHandler();
        HttpServletRequest request = request("/api/patients/missing");

        // ------------------- Act -------------------
        ResponseEntity<com.champsoft.healthcare.patients.web.ApiErrorResponse> response =
                handler.notFound(new PatientNotFoundException("Patient not found"), request);

        // ------------------- Assert -------------------
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("Patient not found");
        assertThat(response.getBody().path()).isEqualTo("/api/patients/missing");
    }

    @Test
    void shouldDirectlyTestDuplicatePatientExceptionHandler() {

        // ------------------- Arrange -------------------
        PatientExceptionHandler handler = new PatientExceptionHandler();
        HttpServletRequest request = request("/api/patients");

        // ------------------- Act -------------------
        ResponseEntity<com.champsoft.healthcare.patients.web.ApiErrorResponse> response =
                handler.conflict(new DuplicatePatientException("Patient already exists"), request);

        // ------------------- Assert -------------------
        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().error()).isEqualTo("Conflict");
        assertThat(response.getBody().message()).isEqualTo("Patient already exists");
    }

    @Test
    void shouldDirectlyTestInvalidInsuranceCardExceptionHandler() {

        // ------------------- Arrange -------------------
        PatientExceptionHandler handler = new PatientExceptionHandler();
        HttpServletRequest request = request("/api/patients");

        // ------------------- Act -------------------
        ResponseEntity<com.champsoft.healthcare.patients.web.ApiErrorResponse> response =
                handler.badRequest(new InvalidInsuranceCardNumber("Invalid card"), request);

        // ------------------- Assert -------------------
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).isEqualTo("Invalid card");
    }

    @Test
    void shouldDirectlyTestGlobalExceptionHandlerWithMessage() {

        // ------------------- Arrange -------------------
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = request("/api/patients/test");

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
        HttpServletRequest request = request("/api/patients/test");

        // ------------------- Act -------------------
        ResponseEntity<ApiErrorResponse> response =
                handler.handleAny(new RuntimeException(), request);

        // ------------------- Assert -------------------
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Unexpected error");
    }

    // Helper: creates a fake HttpServletRequest with the given URI path.
    private HttpServletRequest request(String path) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(path);
        return req;
    }

    // Helper: creates a valid patient through the API and returns the generated ID.
    private String createPatientAndReturnId(String phone, String email, String cardNum) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/patients")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Test",
                                  "lastName": "Patient",
                                  "phoneNumber": "%s",
                                  "email": "%s",
                                  "dateOfBirth": "1990-01-15",
                                  "healthCardNum": "%s",
                                  "expiryDate": "2030-12-31",
                                  "streetNumber": 1,
                                  "streetName": "Main St",
                                  "city": "Montreal",
                                  "postalCode": "H1A1A1",
                                  "Country": "Canada"
                                }
                                """.formatted(phone, email, cardNum)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asText();
    }
}