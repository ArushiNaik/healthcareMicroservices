package com.champsoft.healthcare.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Route configuration test → verifies the Gateway routes are configured correctly.
// Tests route IDs, URIs, and that all expected routes exist.
// Uses the "testing" profile → dummy downstream URIs (localhost:990x).
// No real downstream services are started.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testing")
class GatewayRouteConfigTest {

    // RouteLocator → Spring Cloud Gateway bean holding all configured routes.
    @Autowired
    private RouteLocator routeLocator;

    // -----------------------------------------------------------------------
    // Helper: collect all route IDs from the RouteLocator reactively.
    // -----------------------------------------------------------------------
    private List<String> routeIds() {
        return routeLocator.getRoutes()
                .map(Route::getId)
                .collectList()
                .block();
    }

    // -----------------------------------------------------------------------
    // Helper: find a single route by ID.
    // -----------------------------------------------------------------------
    private Route findRoute(String id) {
        return routeLocator.getRoutes()
                .filter(r -> id.equals(r.getId()))
                .blockFirst();
    }

    @Test
    @DisplayName("Should have exactly four configured routes")
    void shouldHaveFourConfiguredRoutes() {

        // ------------------- Act -------------------
        List<String> ids = routeIds();

        // ------------------- Assert -------------------
        assertThat(ids).isNotNull();
        assertThat(ids).hasSize(4);
    }

    @Test
    @DisplayName("Should have all four expected route IDs")
    void shouldHaveAllFourExpectedRouteIds() {

        // ------------------- Act -------------------
        List<String> ids = routeIds();

        // ------------------- Assert -------------------
        assertThat(ids).containsExactlyInAnyOrder(
                "doctors-service",
                "patients-service",
                "billing-service",
                "appointments-service"
        );
    }

    @Test
    @DisplayName("Should have doctors-service route")
    void shouldHaveDoctorsServiceRoute() {

        // ------------------- Act -------------------
        List<String> ids = routeIds();

        // ------------------- Assert -------------------
        assertThat(ids).contains("doctors-service");
    }

    @Test
    @DisplayName("Should have patients-service route")
    void shouldHavePatientsServiceRoute() {

        // ------------------- Act -------------------
        List<String> ids = routeIds();

        // ------------------- Assert -------------------
        assertThat(ids).contains("patients-service");
    }

    @Test
    @DisplayName("Should have billing-service route")
    void shouldHaveBillingServiceRoute() {

        // ------------------- Act -------------------
        List<String> ids = routeIds();

        // ------------------- Assert -------------------
        assertThat(ids).contains("billing-service");
    }

    @Test
    @DisplayName("Should have appointments-service route")
    void shouldHaveAppointmentsServiceRoute() {

        // ------------------- Act -------------------
        List<String> ids = routeIds();

        // ------------------- Assert -------------------
        assertThat(ids).contains("appointments-service");
    }

    @Test
    @DisplayName("Should have doctors-service route with correct URI")
    void shouldHaveDoctorsServiceRouteWithCorrectUri() {

        // ------------------- Act -------------------
        Route route = findRoute("doctors-service");

        // ------------------- Assert -------------------
        assertThat(route).isNotNull();
        // URI comes from application-testing.yml
        assertThat(route.getUri().toString()).isEqualTo("http://localhost:9901");
    }

    @Test
    @DisplayName("Should have patients-service route with correct URI")
    void shouldHavePatientsServiceRouteWithCorrectUri() {

        // ------------------- Act -------------------
        Route route = findRoute("patients-service");

        // ------------------- Assert -------------------
        assertThat(route).isNotNull();
        assertThat(route.getUri().toString()).isEqualTo("http://localhost:9902");
    }

    @Test
    @DisplayName("Should have billing-service route with correct URI")
    void shouldHaveBillingServiceRouteWithCorrectUri() {

        // ------------------- Act -------------------
        Route route = findRoute("billing-service");

        // ------------------- Assert -------------------
        assertThat(route).isNotNull();
        assertThat(route.getUri().toString()).isEqualTo("http://localhost:9903");
    }

    @Test
    @DisplayName("Should have appointments-service route with correct URI")
    void shouldHaveAppointmentsServiceRouteWithCorrectUri() {

        // ------------------- Act -------------------
        Route route = findRoute("appointments-service");

        // ------------------- Assert -------------------
        assertThat(route).isNotNull();
        assertThat(route.getUri().toString()).isEqualTo("http://localhost:9904");
    }
}
