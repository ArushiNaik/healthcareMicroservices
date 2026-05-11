package com.champsoft.healthcare.appointments.infrastructure.ACL;

import com.champsoft.healthcare.appointments.application.exceptions.CrossContextValidationException;
import com.champsoft.healthcare.appointments.application.port.out.BillingPort;
import com.champsoft.healthcare.appointments.application.port.out.PatientEligibilityPort;
import com.champsoft.healthcare.appointments.infrastructure.ACL.DoctorEligibilityRestAdapter;
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
// Tests that DoctorEligibilityRestAdapter calls the correct URL,
// uses GET method, and handles all HTTP response scenarios.
//
// application-testing.yml sets: services.doctors.base-url=http://localhost:9999
// MockRestServiceServer intercepts all RestTemplate calls → no real HTTP.
@SpringBootTest
@ActiveProfiles("testing")
class DoctorEligibilityRestAdapterTest {

    // The REAL adapter under test (not mocked)
    @Autowired
    private DoctorEligibilityRestAdapter adapter;

    // The REAL RestTemplate bean — MockRestServiceServer binds to this
    @Autowired
    private RestTemplate restTemplate;

    // Replace the other two ports so they don't interfere
    @MockitoBean
    private PatientEligibilityPort patientEligibilityPort;

    @MockitoBean
    private BillingPort billingPort;

    // MockRestServiceServer intercepts all RestTemplate HTTP calls
    private MockRestServiceServer mockServer;

    // Base URL from application-testing.yml: services.doctors.base-url=http://localhost:9999
    private static final String BASE = "http://localhost:9999";

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("Should return true when doctor eligibility endpoint returns true")
    void shouldReturnTrueWhenDoctorIsEligible() {

        // ------------------- Arrange -------------------
        // Adapter calls: GET {base}/api/doctors/{id}/eligibility
        mockServer
                .expect(requestTo(BASE + "/api/doctors/doctor-1/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        boolean result = adapter.exists("doctor-1");

        // ------------------- Assert -------------------
        assertThat(result).isTrue();
        mockServer.verify();
    }

    @Test
    @DisplayName("Should return false when doctor eligibility endpoint returns false")
    void shouldReturnFalseWhenDoctorIsNotEligible() {

        // ------------------- Arrange -------------------
        mockServer
                .expect(requestTo(BASE + "/api/doctors/doctor-inactive/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("false", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        boolean result = adapter.exists("doctor-inactive");

        // ------------------- Assert -------------------
        assertThat(result).isFalse();
        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw CrossContextValidationException when doctor returns 404")
    void shouldThrowCrossContextExceptionWhenDoctorNotFound() {

        // ------------------- Arrange -------------------
        // Adapter catches HttpClientErrorException.NotFound → throws CrossContextValidationException
        mockServer
                .expect(requestTo(BASE + "/api/doctors/doctor-missing/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(NOT_FOUND));

        // ------------------- Act + Assert -------------------
        assertThrows(CrossContextValidationException.class,
                () -> adapter.exists("doctor-missing"));

        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw CrossContextValidationException when doctor service returns 500")
    void shouldThrowCrossContextExceptionWhenDoctorServiceErrors() {

        // ------------------- Arrange -------------------
        // Adapter catches generic Exception → throws CrossContextValidationException("Doctor service is unavailable")
        mockServer
                .expect(requestTo(BASE + "/api/doctors/doctor-error/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        // ------------------- Act + Assert -------------------
        assertThrows(CrossContextValidationException.class,
                () -> adapter.exists("doctor-error"));

        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw CrossContextValidationException when doctor service returns 503")
    void shouldThrowCrossContextExceptionWhenDoctorServiceUnavailable() {

        // ------------------- Arrange -------------------
        mockServer
                .expect(requestTo(BASE + "/api/doctors/doctor-down/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(SERVICE_UNAVAILABLE));

        // ------------------- Act + Assert -------------------
        assertThrows(CrossContextValidationException.class,
                () -> adapter.exists("doctor-down"));

        mockServer.verify();
    }

    @Test
    @DisplayName("Should use GET HTTP method when calling doctor eligibility")
    void shouldUseGetMethodForDoctorEligibilityCall() {

        // ------------------- Arrange -------------------
        // andExpect(method(HttpMethod.GET)) fails the test if any other method is used
        mockServer
                .expect(requestTo(BASE + "/api/doctors/doctor-method-test/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        adapter.exists("doctor-method-test");

        // ------------------- Assert -------------------
        mockServer.verify();
    }

    @Test
    @DisplayName("Should call correct URL containing the provided doctor ID")
    void shouldCallCorrectUrlWithDoctorId() {

        // ------------------- Arrange -------------------
        String doctorId = "11111111-1111-1111-1111-111111111111";
        mockServer
                .expect(requestTo(BASE + "/api/doctors/" + doctorId + "/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        adapter.exists(doctorId);

        // ------------------- Assert -------------------
        // mockServer.verify() confirms the exact URL was called
        mockServer.verify();
    }
}
