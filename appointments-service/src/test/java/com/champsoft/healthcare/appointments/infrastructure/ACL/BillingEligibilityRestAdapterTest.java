package com.champsoft.healthcare.appointments.infrastructure.ACL;

import com.champsoft.healthcare.appointments.application.exceptions.CrossContextValidationException;
import com.champsoft.healthcare.appointments.application.port.out.DoctorEligibilityPort;
import com.champsoft.healthcare.appointments.application.port.out.PatientEligibilityPort;
import com.champsoft.healthcare.appointments.infrastructure.ACL.BillingEligibilityRestAdapter;
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
// Tests that BillingEligibilityRestAdapter calls the correct URL,
// uses GET method, and handles all HTTP response scenarios.
//
// application-testing.yml sets: services.billing.base-url=http://localhost:9997
// Adapter URL pattern: {base}/api/billing/{id}/eligibility  (singular "billing")
@SpringBootTest
@ActiveProfiles("testing")
class BillingEligibilityRestAdapterTest {

    // The REAL adapter under test (not mocked)
    @Autowired
    private BillingEligibilityRestAdapter adapter;

    // The REAL RestTemplate bean — MockRestServiceServer binds to this
    @Autowired
    private RestTemplate restTemplate;

    // Replace the other two ports so they don't interfere
    @MockitoBean
    private DoctorEligibilityPort doctorEligibilityPort;

    @MockitoBean
    private PatientEligibilityPort patientEligibilityPort;

    // MockRestServiceServer intercepts all RestTemplate HTTP calls
    private MockRestServiceServer mockServer;

    // Base URL from application-testing.yml: services.billing.base-url=http://localhost:9997
    // Adapter builds: billingBaseUrl + "/api/billing/" + billingId + "/eligibility"
    private static final String BASE = "http://localhost:9997";

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("Should return true when billing eligibility endpoint returns true")
    void shouldReturnTrueWhenBillingExists() {

        // ------------------- Arrange -------------------
        // Adapter calls: GET {base}/api/billing/{id}/eligibility
        mockServer
                .expect(requestTo(BASE + "/api/billing/billing-1/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        boolean result = adapter.exists("billing-1");

        // ------------------- Assert -------------------
        assertThat(result).isTrue();
        mockServer.verify();
    }

    @Test
    @DisplayName("Should return false when billing eligibility endpoint returns false")
    void shouldReturnFalseWhenBillingNotEligible() {

        // ------------------- Arrange -------------------
        // Billing is PENDING (not PAID) → eligibility returns false
        mockServer
                .expect(requestTo(BASE + "/api/billing/billing-unpaid/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("false", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        boolean result = adapter.exists("billing-unpaid");

        // ------------------- Assert -------------------
        assertThat(result).isFalse();
        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw CrossContextValidationException when billing returns 404")
    void shouldThrowCrossContextExceptionWhenBillingNotFound() {

        // ------------------- Arrange -------------------
        // Adapter catches HttpClientErrorException.NotFound → throws CrossContextValidationException
        mockServer
                .expect(requestTo(BASE + "/api/billing/billing-missing/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(NOT_FOUND));

        // ------------------- Act + Assert -------------------
        assertThrows(CrossContextValidationException.class,
                () -> adapter.exists("billing-missing"));

        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw CrossContextValidationException when billing service returns 500")
    void shouldThrowCrossContextExceptionWhenBillingServiceErrors() {

        // ------------------- Arrange -------------------
        // Adapter catches generic Exception → throws CrossContextValidationException("Billing service is unavailable")
        mockServer
                .expect(requestTo(BASE + "/api/billing/billing-error/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        // ------------------- Act + Assert -------------------
        assertThrows(CrossContextValidationException.class,
                () -> adapter.exists("billing-error"));

        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw CrossContextValidationException when billing service returns 503")
    void shouldThrowCrossContextExceptionWhenBillingServiceUnavailable() {

        // ------------------- Arrange -------------------
        mockServer
                .expect(requestTo(BASE + "/api/billing/billing-down/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(SERVICE_UNAVAILABLE));

        // ------------------- Act + Assert -------------------
        assertThrows(CrossContextValidationException.class,
                () -> adapter.exists("billing-down"));

        mockServer.verify();
    }

    @Test
    @DisplayName("Should use GET HTTP method when calling billing eligibility")
    void shouldUseGetMethodForBillingEligibilityCall() {

        // ------------------- Arrange -------------------
        mockServer
                .expect(requestTo(BASE + "/api/billing/billing-method-test/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        adapter.exists("billing-method-test");

        // ------------------- Assert -------------------
        mockServer.verify();
    }

    @Test
    @DisplayName("Should call correct URL containing the provided billing ID")
    void shouldCallCorrectUrlWithBillingId() {

        // ------------------- Arrange -------------------
        String billingId = "billing-url-check";
        mockServer
                .expect(requestTo(BASE + "/api/billing/" + billingId + "/eligibility"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.APPLICATION_JSON));

        // ------------------- Act -------------------
        adapter.exists(billingId);

        // ------------------- Assert -------------------
        mockServer.verify();
    }
}
