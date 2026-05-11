package com.champsoft.healthcare.appointments;

import com.champsoft.healthcare.appointments.application.port.out.BillingPort;
import com.champsoft.healthcare.appointments.application.port.out.DoctorEligibilityPort;
import com.champsoft.healthcare.appointments.application.port.out.PatientEligibilityPort;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

// Context-load test → verifies the Spring Boot application context starts
// successfully using the H2 testing profile.
// @MockitoBean replaces the 3 REST adapters that would call external services.
@SpringBootTest
@ActiveProfiles("testing")
class AppointmentsServiceApplicationTests {

	// Replace the 3 external REST adapters with mocks so they do not
	// attempt to connect to real services during context load.
	@MockitoBean
	private DoctorEligibilityPort doctorEligibilityPort;

	@MockitoBean
	private PatientEligibilityPort patientEligibilityPort;

	@MockitoBean
	private BillingPort billingPort;

	@Test
	void contextLoads() {
		// If the application context fails to start, this test fails.
		// This catches missing beans, bad config, and circular dependencies.
	}
}
