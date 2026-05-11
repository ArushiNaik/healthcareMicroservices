package com.champsoft.healthcare.appointments.infrastructure.ACL;

import com.champsoft.healthcare.appointments.application.exceptions.CrossContextValidationException;
import com.champsoft.healthcare.appointments.application.port.out.BillingPort;
import com.champsoft.healthcare.appointments.application.port.out.DoctorEligibilityPort;
import com.champsoft.healthcare.appointments.infrastructure.ACL.PatientEligibilityRestAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

// Level 7 — Outbound REST Adapter Test
// Tests that PatientEligibilityRestAdapter calls the correct URL,
// uses GET method, and handles all HTTP response scenarios.
//
// application-testing.yml sets: services.patients.base-url=http://localhost:9998
@SpringBootTest
@ActiveProfiles("testing")
class PatientEligibilityRestAdapterTest {

    // The REAL adapter under test (not mocked)
    @Autowired
    private PatientEligibilityRestAdapter adapter;

    // The REAL RestTemplate bean — MockRestServiceServer binds to this
    @Autowired
    private RestTemplate restTemplate;

    // Replace the other two ports so they don't interfere
    @MockitoBean
    private DoctorEligibilityPort doctorEligibilityPort;

    @MockitoBean
    private BillingPort billingPort;

    // MockRestServiceServer intercepts all RestTemplate HTTP calls
    private MockRestServiceServer mockServer;

    // Base URL from application-testing.yml: services.patients.base-url=http://localhost:9998
    private static final String BASE = "http://localhost:9998";

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("Should return true when patient eligibility endpoint returns true")
    void shouldReturnTrueWhenPatientIsEligible() {

        // ------------------- Arrange -------------------
        // Adapter calls: GET {base}/api/patients/{id}/eligibility
        mockServer
                .expect(requestTo(BASE + "/api/patients/patient-1/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        boolean result = adapter.exists("patient-1");

        // ------------------- Assert -------------------
        assertThat(result).isTrue();
        mockServer.verify();
    }

    @Test
    @DisplayName("Should return false when patient eligibility endpoint returns false")
    void shouldReturnFalseWhenPatientIsNotEligible() {

        // ------------------- Arrange -------------------
        // Patient is a minor → eligibility returns false
        mockServer
                .expect(requestTo(BASE + "/api/patients/patient-minor/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("false", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        boolean result = adapter.exists("patient-minor");

        // ------------------- Assert -------------------
        assertThat(result).isFalse();
        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw CrossContextValidationException when patient returns 404")
    void shouldThrowCrossContextExceptionWhenPatientNotFound() {

        // ------------------- Arrange -------------------
        // Adapter catches HttpClientErrorException.NotFound → throws CrossContextValidationException
        mockServer
                .expect(requestTo(BASE + "/api/patients/patient-missing/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(NOT_FOUND));

        // ------------------- Act + Assert -------------------
        assertThrows(CrossContextValidationException.class,
                () -> adapter.exists("patient-missing"));

        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw CrossContextValidationException when patient service returns 500")
    void shouldThrowCrossContextExceptionWhenPatientServiceErrors() {

        // ------------------- Arrange -------------------
        // Adapter catches generic Exception → throws CrossContextValidationException("Patient service is unavailable")
        mockServer
                .expect(requestTo(BASE + "/api/patients/patient-error/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        // ------------------- Act + Assert -------------------
        assertThrows(CrossContextValidationException.class,
                () -> adapter.exists("patient-error"));

        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw CrossContextValidationException when patient service returns 503")
    void shouldThrowCrossContextExceptionWhenPatientServiceUnavailable() {

        // ------------------- Arrange -------------------
        mockServer
                .expect(requestTo(BASE + "/api/patients/patient-down/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(SERVICE_UNAVAILABLE));

        // ------------------- Act + Assert -------------------
        assertThrows(CrossContextValidationException.class,
                () -> adapter.exists("patient-down"));

        mockServer.verify();
    }

    @Test
    @DisplayName("Should use GET HTTP method when calling patient eligibility")
    void shouldUseGetMethodForPatientEligibilityCall() {

        // ------------------- Arrange -------------------
        mockServer
                .expect(requestTo(BASE + "/api/patients/patient-method-test/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        adapter.exists("patient-method-test");

        // ------------------- Assert -------------------
        mockServer.verify();
    }

    @Test
    @DisplayName("Should call correct URL containing the provided patient ID")
    void shouldCallCorrectUrlWithPatientId() {

        // ------------------- Arrange -------------------
        String patientId = "patient-url-check";
        mockServer
                .expect(requestTo(BASE + "/api/patients/" + patientId + "/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        adapter.exists(patientId);

        // ------------------- Assert -------------------
        mockServer.verify();
    }
}
