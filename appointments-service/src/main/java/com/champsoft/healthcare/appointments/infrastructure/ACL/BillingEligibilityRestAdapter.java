package com.champsoft.healthcare.appointments.infrastructure.ACL;

import com.champsoft.healthcare.appointments.application.exceptions.CrossContextValidationException;
import com.champsoft.healthcare.appointments.application.port.out.BillingPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Component
public class BillingEligibilityRestAdapter implements BillingPort {

    private final RestTemplate restTemplate;
    @Value("{services.billing.base-url}")
    private String billingBaseUrl;

    public BillingEligibilityRestAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean exists(String billingId) {
        String url = billingBaseUrl +"/api/billing"+ billingBaseUrl +"/eligibility";
        try{
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(result);
        }catch(HttpClientErrorException.NotFound ex){
            throw new CrossContextValidationException("Billing not found"+ billingId);
        }catch(HttpClientErrorException ex){
            throw new CrossContextValidationException("Billing validation failed"+ billingId);
        }catch(Exception ex){
            throw new CrossContextValidationException("Billing service is unavailable");
        }

    }
}




//private final BillingCrudService billingService;
//
//    public BillingEligibilityRestAdapter(BillingCrudService billingService) {
//        this.billingService = billingService;
//    }
//
//    @Override
//    public void createBill(String appointmentId, String patientId) {
//        // prepare raw arguments for BillingCrudService
//        String description = "Billing for appointment " + appointmentId;
//        double amount = 100.0;  // or calculate dynamically
//        DueDate dueDate = new DueDate(LocalDate.now().plusDays(7));
//        PaymentMethod method = PaymentMethod.CREDIT_CARD;
//
//        // call the service with expected argument list
//        billingService.create(description, amount, dueDate, method);
//    }