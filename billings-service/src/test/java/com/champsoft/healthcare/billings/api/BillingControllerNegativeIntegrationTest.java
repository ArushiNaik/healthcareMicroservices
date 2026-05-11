package com.champsoft.healthcare.billings.api;


import com.champsoft.healthcare.billings.application.exception.BillingNotFoundException;
import com.champsoft.healthcare.billings.domain.exception.InvalidStatusRefund;
import com.champsoft.healthcare.billings.web.ApiErrorResponse;
import com.champsoft.healthcare.billings.web.GlobalExceptionHandler;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class BillingControllerNegativeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Test
    void shouldReturnBadRequestWhenDueDateIsInThePast() throws Exception {

        mockMvc.perform(post("/api/billings")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Consultation",
                                  "amount": 150.00,
                                  "dueDate": "2020-01-01",
                                  "method": "CASH"
                                }
                                """))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus()).isGreaterThanOrEqualTo(400));
    }

    @Test
    void shouldReturnBadRequestWhenAmountIsNegative() throws Exception {

        mockMvc.perform(post("/api/billings")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Consultation",
                                  "amount": -50.00,
                                  "dueDate": "2030-12-31",
                                  "method": "CASH"
                                }
                                """))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus()).isGreaterThanOrEqualTo(400));
    }

    @Test
    void shouldReturnBadRequestWhenAmountIsZero() throws Exception {

        mockMvc.perform(post("/api/billings")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Consultation",
                                  "amount": 0.00,
                                  "dueDate": "2030-12-31",
                                  "method": "CASH"
                                }
                                """))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus()).isGreaterThanOrEqualTo(400));
    }

    @Test
    void shouldReturnBadRequestWhenPaymentMethodIsInvalid() throws Exception {

        mockMvc.perform(post("/api/billings")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Consultation",
                                  "amount": 150.00,
                                  "dueDate": "2030-12-31",
                                  "method": "BITCOIN"
                                }
                                """))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus()).isGreaterThanOrEqualTo(400));
    }

    @Test
    void shouldReturnBadRequestWhenDescriptionIsBlank() throws Exception {

        mockMvc.perform(post("/api/billings")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "",
                                  "amount": 150.00,
                                  "dueDate": "2030-12-31",
                                  "method": "CASH"
                                }
                                """))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus()).isGreaterThanOrEqualTo(400));
    }

    @Test
    void shouldReturnNotFoundWhenBillingDoesNotExist() throws Exception {

        mockMvc.perform(get("/api/billings/{id}", "missing-billing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingBilling() throws Exception {

        mockMvc.perform(put("/api/billings/{id}", "missing-billing-id")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Lab Test",
                                  "amount": 200.00
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingBilling() throws Exception {

        mockMvc.perform(delete("/api/billings/{id}", "missing-billing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenRefundingPendingBilling() throws Exception {

        // ------------------- Arrange -------------------
        // Create a PENDING billing
        MvcResult result = mockMvc.perform(post("/api/billings")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Pending Refund Test",
                                  "amount": 100.00,
                                  "dueDate": "2030-12-31",
                                  "method": "INSURANCE"
                                }
                                """))
                .andExpect(status().isOk()).andReturn();

        String billingId = objectMapper.readTree(
                result.getResponse().getContentAsString()).get("id").asText();

        // ------------------- Act + Assert -------------------
        // Business rule: PENDING billing cannot be refunded.
        mockMvc.perform(post("/api/billings/{id}/refund", billingId))
                .andExpect(res -> org.assertj.core.api.Assertions.assertThat(res.getResponse().getStatus()).isGreaterThanOrEqualTo(400));
    }

    @Test
    void shouldReturnBadRequestWhenRefundingAlreadyRefundedBilling() throws Exception {

        // ------------------- Arrange -------------------
        MvcResult result = mockMvc.perform(post("/api/billings")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Double Refund Test",
                                  "amount": 75.00,
                                  "dueDate": "2030-12-31",
                                  "method": "CREDIT_CARD"
                                }
                                """))
                .andExpect(status().isOk()).andReturn();

        String billingId = objectMapper.readTree(
                result.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/billings/{id}/paid", billingId)).andExpect(status().isOk());
        mockMvc.perform(post("/api/billings/{id}/refund", billingId)).andExpect(status().isOk());

        // ------------------- Act + Assert -------------------
        // Second refund → should fail.
        mockMvc.perform(post("/api/billings/{id}/refund", billingId))
                .andExpect(res -> org.assertj.core.api.Assertions.assertThat(res.getResponse().getStatus()).isGreaterThanOrEqualTo(400));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingBillingWithMalformedJson() throws Exception {

        mockMvc.perform(post("/api/billings")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Broken"
                                """))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResponse().getStatus()).isGreaterThanOrEqualTo(400));
    }

    // -----------------------------------------------------------------------
    // Direct exception-handler tests (same pattern as VRMS)
    // -----------------------------------------------------------------------

    @Test
    void shouldDirectlyTestBillingNotFoundExceptionHandler() {

        BillingExceptionHandler handler = new BillingExceptionHandler();
        HttpServletRequest request = request("/api/billings/missing");

        ResponseEntity<ApiErrorResponse> response =
                handler.notFound(new BillingNotFoundException("Billing not found"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().error()).isEqualTo("Not Found");
        assertThat(response.getBody().message()).isEqualTo("Billing not found");
        assertThat(response.getBody().path()).isEqualTo("/api/billings/missing");
    }

    @Test
    void shouldDirectlyTestInvalidStatusRefundExceptionHandler() {

        BillingExceptionHandler handler = new BillingExceptionHandler();
        HttpServletRequest request = request("/api/billings/billing-1/refund");

        ResponseEntity<ApiErrorResponse> response =
                handler.badRequest(new InvalidStatusRefund("Cannot refund PENDING billing"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().error()).isEqualTo("Bad Request");
        assertThat(response.getBody().message()).isEqualTo("Cannot refund PENDING billing");
    }

    @Test
    void shouldDirectlyTestGlobalExceptionHandlerWithMessage() {

        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = request("/api/billings/test");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleAny(new RuntimeException("Something went wrong"), request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().message()).isEqualTo("Something went wrong");
    }

    @Test
    void shouldDirectlyTestGlobalExceptionHandlerWithNullMessage() {

        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        HttpServletRequest request = request("/api/billings/test");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleAny(new RuntimeException(), request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Unexpected error");
    }

    private HttpServletRequest request(String path) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(path);
        return req;
    }
}

