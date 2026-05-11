package com.champsoft.healthcare.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Context-load test → verifies the Spring Boot Gateway application context
// starts successfully with the "testing" profile.
// No H2, no database — gateway has no persistence layer.
// webEnvironment = RANDOM_PORT prevents port conflicts.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testing")
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
		// If the gateway context fails to start, this test fails.
		// This catches missing beans, bad route config, circular dependencies.
	}
}
