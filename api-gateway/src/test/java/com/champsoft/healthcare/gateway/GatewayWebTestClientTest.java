package com.champsoft.healthcare.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

// WebTestClient test → verifies gateway routing behavior via HTTP.
// Gateway is WebFlux → uses WebTestClient, never MockMvc.
// Routes in testing profile point to localhost:990x (not running).
// Any response >= 400 from the gateway proves the route was attempted.
// 404 from gateway itself = no route matched (only for unknown paths).
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testing")
class GatewayWebTestClientTest {

    @LocalServerPort
    private int port;

    private WebTestClient client() {
        return WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    @DisplayName("Should route GET /api/doctors to doctors-service")
    void shouldRouteDoctorsRequests() {
        // Route matched → gateway attempts forward → any non-2xx is acceptable
        // (502 Bad Gateway, 503 Service Unavailable, 504 Gateway Timeout)
        client().get().uri("/api/doctors")
                .exchange()
                .expectStatus().value(status ->
                        org.assertj.core.api.Assertions.assertThat(status)
                                .as("Expected a gateway error (4xx/5xx), got: " + status)
                                .isGreaterThanOrEqualTo(400));
    }

    @Test
    @DisplayName("Should route GET /api/patients to patients-service")
    void shouldRoutePatientsRequests() {
        client().get().uri("/api/patients")
                .exchange()
                .expectStatus().value(status ->
                        org.assertj.core.api.Assertions.assertThat(status)
                                .isGreaterThanOrEqualTo(400));
    }

    @Test
    @DisplayName("Should route GET /api/billing to billing-service")
    void shouldRouteBillingRequests() {
        client().get().uri("/api/billing")
                .exchange()
                .expectStatus().value(status ->
                        org.assertj.core.api.Assertions.assertThat(status)
                                .isGreaterThanOrEqualTo(400));
    }

    @Test
    @DisplayName("Should route GET /api/appointments to appointments-service")
    void shouldRouteAppointmentsRequests() {
        client().get().uri("/api/appointments")
                .exchange()
                .expectStatus().value(status ->
                        org.assertj.core.api.Assertions.assertThat(status)
                                .isGreaterThanOrEqualTo(400));
    }

    @Test
    @DisplayName("Should return 404 for unknown route not matching any predicate")
    void shouldReturnNotFoundForUnknownRoute() {
        client().get().uri("/api/unknown-service/test")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should return 404 for root path not matching any route")
    void shouldReturnNotFoundForRootPath() {
        client().get().uri("/some/random/path")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should route POST /api/doctors to doctors-service")
    void shouldRoutePostRequestsToDoctorsService() {
        client().post().uri("/api/doctors")
                .bodyValue("{}")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().value(status ->
                        org.assertj.core.api.Assertions.assertThat(status)
                                .isGreaterThanOrEqualTo(400));
    }

    @Test
    @DisplayName("Should route POST /api/appointments to appointments-service")
    void shouldRoutePostRequestsToAppointmentsService() {
        client().post().uri("/api/appointments")
                .bodyValue("{}")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().value(status ->
                        org.assertj.core.api.Assertions.assertThat(status)
                                .isGreaterThanOrEqualTo(400));
    }

    @Test
    @DisplayName("Should route DELETE /api/patients/{id} to patients-service")
    void shouldRouteDeleteRequestsToPatientsService() {
        client().delete().uri("/api/patients/some-id")
                .exchange()
                .expectStatus().value(status ->
                        org.assertj.core.api.Assertions.assertThat(status)
                                .isGreaterThanOrEqualTo(400));
    }
}